import {
  ConflictException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Agency } from 'src/agency/agency.entity';
import { DataSource, EntityTarget, QueryRunner, Repository } from 'typeorm';
import { CreateAgencyDto } from './dto/create-agency.dto';
import { Manager } from 'src/agency-manager/agency-manager.entity';
import { User } from 'src/auth/user.entity';
import { UserRoles } from 'src/common/types/user-roles';
import * as bcrypt from 'bcrypt';
import { UserItem } from 'src/common/types/userItem';
import { ConfigService } from '@nestjs/config';
import { Provider } from 'src/common/types/provider.enum';
import { CreateAgencyResponse } from './types/create-agency-response';
import { Agent } from 'src/agent/agent.entity';
import { SupportAdmin } from 'src/support-admin/support-admin.entity';

interface HasAgency {
  agency: { id: string };
  userId: string;
}

@Injectable()
export class AdminService {
  constructor(
    @InjectRepository(Agency)
    private readonly agencyRepository: Repository<Agency>,

    @InjectRepository(Manager)
    private readonly managerRepository: Repository<Manager>,

    @InjectRepository(User)
    private readonly userRepository: Repository<User>,

    private readonly configService: ConfigService,

    private readonly dataSource: DataSource,
  ) {}

  async createAgency(
    createAgencyDto: CreateAgencyDto,
  ): Promise<CreateAgencyResponse> {
    const {
      name,
      legalAddress,
      phone,
      vatNumber,
      managerName,
      managerSurname,
      managerEmail,
      managerPhone,
    } = createAgencyDto;

    const queryRunner = this.dataSource.createQueryRunner();

    await queryRunner.connect();
    await queryRunner.startTransaction();

    try {
      const exists = await queryRunner.manager.findOne(Agency, {
        where: [{ name }, { phone }, { vatNumber }],
      });

      if (exists) throw new ConflictException('Agency already exists');

      const agency = queryRunner.manager.create(Agency, {
        name,
        legalAddress,
        phone,
        vatNumber,
      });

      await queryRunner.manager.save(agency);

      const defaultPassword = this.configService.get<string>(
        'DEFAULT_PASSWORD',
        'Manager1234!',
      );
      const hashedPassword = await this.hashPassword(defaultPassword);

      const userExist = await queryRunner.manager.findOne(User, {
        where: [{ email: managerEmail }, { phone: managerPhone }],
      });

      if (userExist)
        throw new ConflictException(
          `An user with email: ${managerEmail} or phone: ${managerPhone} already exists`,
        );

      const userManager = queryRunner.manager.create(User, {
        name: managerName,
        surname: managerSurname,
        email: managerEmail,
        password: hashedPassword,
        phone: managerPhone,
        role: UserRoles.MANAGER,
        provider: Provider.LOCAL,
      });

      await queryRunner.manager.save(userManager);

      const manager = queryRunner.manager.create(Manager, {
        userId: userManager.id,
        agency: agency,
      });

      await queryRunner.manager.save(manager);

      await queryRunner.commitTransaction();

      const user: UserItem = userManager;
      user.manager = manager;

      return {
        message: 'Agency created correctly',
        agency: agency,
        manager: user,
      };
    } catch (err) {
      await queryRunner.rollbackTransaction();
      throw err;
    } finally {
      await queryRunner.release();
    }
  }

  async removeAgencyById(agencyId: string) {
    const queryRunner = this.dataSource.createQueryRunner();

    await queryRunner.connect();
    await queryRunner.startTransaction();

    try {
      const agency = await this.findAgencyOrThrow(queryRunner, agencyId);
      const userManager = await this.findManagerUserOrThrow(
        queryRunner,
        agencyId,
      );

      await this.removeRelatedUsers(queryRunner, Agent, agencyId);
      await this.removeRelatedUsers(queryRunner, SupportAdmin, agencyId);

      await queryRunner.manager.remove(User, userManager);
      await queryRunner.manager.remove(Agency, agency);

      await queryRunner.commitTransaction();

      return {
        message: `Agency ${agency.name} deleted successfully`,
      };
    } catch (err) {
      await queryRunner.rollbackTransaction();
      throw err;
    } finally {
      await queryRunner.release();
    }
  }

  private async findAgencyOrThrow(
    queryRunner: QueryRunner,
    agencyId: string,
  ): Promise<Agency> {
    const agency = await queryRunner.manager.findOne(Agency, {
      where: { id: agencyId },
    });
    if (!agency) throw new NotFoundException('Agency not found');
    return agency;
  }

  private async findManagerUserOrThrow(
    queryRunner: QueryRunner,
    agencyId: string,
  ): Promise<User> {
    const manager = await queryRunner.manager.findOne(Manager, {
      where: { agency: { id: agencyId } },
      relations: ['user'],
    });

    if (!manager) throw new NotFoundException('Manager not found');
    if (!manager.user) throw new NotFoundException('User manager not found');

    return manager.user;
  }

  private async removeRelatedUsers<T extends HasAgency>(
    queryRunner: QueryRunner,
    entity: EntityTarget<T>,
    agencyId: string,
  ) {
    const related = await queryRunner.manager
      .createQueryBuilder(entity, 'e')
      .innerJoin('e.agency', 'agency')
      .where('agency.id = :agencyId', { agencyId })
      .getMany();

    for (const item of related) {
      const user = await queryRunner.manager.findOne(User, {
        where: { id: item.userId },
      });
      if (user) {
        await queryRunner.manager.remove(User, user);
      }
    }
  }

  private async hashPassword(password: string) {
    const salt = await bcrypt.genSalt();
    return bcrypt.hash(password, salt);
  }
}

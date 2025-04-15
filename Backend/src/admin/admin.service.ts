import {
  ConflictException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Agency } from 'src/agency/agency.entity';
import { Repository } from 'typeorm';
import { CreateAgencyDto } from './dto/create-agency.dto';
import { Manager } from 'src/agency-manager/agency-manager.entity';
import { ReplOptions } from 'repl';
import { User } from 'src/auth/user.entity';
import { Roles } from 'src/common/decorator/roles.decorator';
import { UserRoles } from 'src/common/types/user-roles';
import * as bcrypt from 'bcrypt';
import { UserItem } from 'src/common/types/userItem';
import { ConfigService } from '@nestjs/config';

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
  ) {}

  async createAgency(
    createAgencyDto: CreateAgencyDto,
  ): Promise<{ agency: Agency; manager: UserItem }> {
    const {
      name,
      legalAddress,
      phone,
      vatNumber,
      managerName,
      managerSurname,
      managerEmail,
      managerBirthDate,
      managerGender,
      managerPhone,
    } = createAgencyDto;

    const exists = await this.agencyRepository.findOne({
      where: { vatNumber: vatNumber },
    });

    if (exists)
      throw new ConflictException(`This VatNumber ${vatNumber} already exists`);

    const agency = this.agencyRepository.create({
      name,
      legalAddress,
      phone,
      vatNumber,
    });

    const salt = await bcrypt.genSalt();
    const defaultPassword = this.configService.get<string>(
      'DEFAULT_MANAGER_PASSWORD',
      'Manager1234!',
    );
    const hashedPassword = await bcrypt.hash(defaultPassword, salt);

    const found = await this.userRepository
      .createQueryBuilder('user')
      .where('user.email = :email OR user.phone = :phone', {
        email: managerEmail,
        phone: managerPhone,
      })
      .getOne();

    if (found)
      throw new ConflictException(
        `An user with email: ${managerEmail} or phone: ${managerPhone} already exists`,
      );

    await this.agencyRepository.save(agency);

    const userManager = this.userRepository.create({
      name: managerName,
      surname: managerSurname,
      email: managerEmail,
      password: hashedPassword,
      birthDate: managerBirthDate,
      gender: managerGender,
      phone: managerPhone,
      role: UserRoles.MANAGER,
      isDeafaultPassword: true,
    });

    await this.userRepository.save(userManager);

    const manager = this.managerRepository.create({
      userId: userManager.id,
      agency: agency,
    });

    await this.managerRepository.save(manager);

    const user: UserItem = userManager;
    user.manager = manager;

    return {
      agency: agency,
      manager: user,
    };
  }

  async removeAgencyById(agencyId: string) {
    const agency = await this.agencyRepository.findOne({
      where: { id: agencyId },
    });

    if (!agency) throw new NotFoundException('Agency not found');

    const manager = await this.managerRepository.findOne({
      where: { agency: { id: agencyId } },
      relations: ['user'],
    });

    if (!manager) throw new NotFoundException('Manager not found');

    const userManager = await this.userRepository.findOne({
      where: { id: manager.userId },
    });

    if (!userManager) throw new NotFoundException('User manager not found');

    await this.userRepository.delete(userManager.id);

    await this.agencyRepository.delete(agency.id);
    return {
      message: 'Agency delete successfully',
    };
  }
}

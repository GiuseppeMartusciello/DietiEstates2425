import {
  BadRequestException,
  ConflictException,
  Injectable,
  NotFoundException,
  UnauthorizedException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Manager } from './agency-manager.entity';
import { CredentialDto } from './dto/credentials.dto';
import * as bcrypt from 'bcrypt';
import { User } from 'src/auth/user.entity';
import { AuthCredentialDto } from 'src/auth/dto/auth.credentials.dto';
import { UserItem } from 'src/common/types/userItem';
import { SupportAdmin } from 'src/support-admin/support-admin.entity';
import { UserRoles } from 'src/common/types/user-roles';
import { CreateSupportAdminDto } from './dto/create-support-admin.dto';
import { Agent } from 'src/agent/agent.entity';
import { AgentService } from 'src/agent/agent.service';
import { CreateAgentDto } from './dto/create-agent.dto';
import { Agency } from 'src/agency/agency.entity';
import { Provider } from 'src/common/types/provider.enum';
import { CreateSupportAdminResponse } from './types/create-support-admin-response';
import { CreateAgentResponse } from './types/create-agent-response';

@Injectable()
export class AgencyManagerService {
  constructor(
    @InjectRepository(Manager)
    private readonly managerRepository: Repository<Manager>,

    @InjectRepository(User)
    private readonly userRepository: Repository<User>,

    @InjectRepository(Agent)
    private readonly agentRepository: Repository<Agent>,

    @InjectRepository(Agency)
    private readonly agencyRepository: Repository<Agency>,

    @InjectRepository(SupportAdmin)
    private readonly supportAdminRepository: Repository<SupportAdmin>,
  ) {}

  async changePassword(credentials: CredentialDto, userId: string) {
    const { currentPassword, newPassword } = credentials;

    const user = await this.userRepository.findOne({
      where: { id: userId },
    });

    if (!user) throw new NotFoundException('Manager not found');

    const isMatch = await bcrypt.compare(currentPassword, user.password);
    if (!isMatch) throw new UnauthorizedException('Invalid credentials');

    const hashedPassword = await this.hashPassword(newPassword);

    user.password = hashedPassword;
    user.lastPasswordChangeAt = new Date();

    await this.userRepository.save(user);

    return { message: 'Password updated successfully' };
  }

  async createSupportAdmin(
    createSupportAdminDto: CreateSupportAdminDto,
    userManager: UserItem,
  ): Promise<CreateSupportAdminResponse> {
    if (!userManager || !userManager.manager) throw new UnauthorizedException();

    const { name, surname, email, password, birthDate, gender, phone } =
      createSupportAdminDto;

    const agency = userManager.manager.agency;
    if (!agency) throw new BadRequestException(`Agency doesn't exists`);

    const found = await this.userRepository
      .createQueryBuilder('user')
      .where('user.email = :email OR user.phone = :phone', {
        email,
        phone,
      })
      .getOne();

    if (found)
      throw new ConflictException(
        'User with this email or phone already exists',
      );

    const hashedPassword = await this.hashPassword(password);

    const userSupportAdmin = await this.userRepository.create({
      name: name,
      surname: surname,
      email: email,
      password: hashedPassword,
      birthDate: birthDate,
      gender: gender,
      phone: phone,
      role: UserRoles.SUPPORT_ADMIN,
      provider: Provider.LOCAL,
    });

    await this.userRepository.save(userSupportAdmin);

    const supportAdmin = this.supportAdminRepository.create({
      userId: userSupportAdmin.id,
      agency: userManager.manager.agency,
    });

    await this.supportAdminRepository.save(supportAdmin);

    return {
      message: 'Support Admin created successfully',
      supportAdmin: {
        email: userSupportAdmin.email,
        id: userSupportAdmin.id,
        name: userSupportAdmin.name,
        agency: {
          id: agency.id,
          name: agency.name,
        },
      },
    };
  }

  async createAgent(
    createAgentDto: CreateAgentDto,
    agencyId: string,
  ): Promise<CreateAgentResponse> {
    const agency = await this.agencyRepository.findOne({
      where: { id: agencyId },
    });
    if (!agency) {
      throw new BadRequestException(
        `The Agency with ID: ${agencyId} doesn't exist`,
      );
    }

    const {
      licenseNumber,
      name,
      surname,
      email,
      password,
      birthDate,
      gender,
      phone,
      start_date,
      languages,
    } = createAgentDto;

    const existingAgent = await this.agentRepository
      .createQueryBuilder('agent')
      .where('agent.licenseNumber = :licenseNumber', { licenseNumber })
      .getOne();

    if (existingAgent)
      new ConflictException(
        `Agent with license "${licenseNumber}" already exists`,
      );

    const existingUser = await this.userRepository
      .createQueryBuilder('user')
      .where('user.email = :email OR user.phone = :phone', {
        email,
        phone,
      })
      .getOne();

    if (existingUser) throw new ConflictException('User already exists');

    const hashedPassword = await this.hashPassword(password);

    const userAgent = await this.userRepository.create({
      name: name,
      surname: surname,
      email: email,
      password: hashedPassword,
      phone: phone,
      gender: gender,
      birthDate: birthDate,
      role: UserRoles.AGENT,
      provider: Provider.LOCAL,
      lastPasswordChangeAt: new Date(),
    });

    await this.userRepository.save(userAgent);

    const agent = this.agentRepository.create({
      licenseNumber: licenseNumber,
      start_date: start_date,
      languages: languages,
      userId: userAgent.id,
      //user: userAgent,
      agency: agency,
    });

    await this.agentRepository.save(agent);

    return {
      message: 'Agent created successfully',
      agent: {
        email: userAgent.email,
        id: userAgent.id,
        name: userAgent.name,
        licenseNumber: licenseNumber,
        agency: {
          id: agencyId,
          name: agency.name,
        },
      },
    };
  }

  async deleteAgentById(agentId: string) {
    const found = await this.userRepository.findOneBy({ id: agentId });
    if (!found)
      throw new NotFoundException(`Agent with id "${agentId}" not found`);

    await this.userRepository.delete(agentId);

    return {
      message: 'Agent delete successfully',
    };
  }

  private async hashPassword(password: string): Promise<string> {
    const salt = await bcrypt.genSalt();
    return bcrypt.hash(password, salt);
  }
}

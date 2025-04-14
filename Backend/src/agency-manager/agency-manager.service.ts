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
import * as bcrypt from 'bcryptjs';
import { User } from 'src/auth/user.entity';
import { AuthCredentialDto } from 'src/auth/dto/auth.credentials.dto';
import { UserItem } from 'src/common/types/userItem';
import { SupportAdmin } from 'src/support-admin/support-admin.entity';
import { UserRoles } from 'src/common/types/user-roles';
import { CreateSupportAdminDto } from './dto/create-support-admin.dto';
import { Agent } from 'src/agent/agent.entity';
import { AgentService } from 'src/agent/agent.service';
import { CreateAgentDto } from './dto/create-agent.dto';


@Injectable()
export class AgencyManagerService {
  constructor(
    @InjectRepository(Manager)
    private readonly managerRepository: Repository<Manager>,

    @InjectRepository(User)
    private readonly userRepository: Repository<User>,

    @InjectRepository(Agent)
    private readonly agentRepository: Repository<Agent>,

    private readonly agentService: AgentService,

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
    if (!isMatch) throw new UnauthorizedException('Password attuale errata');

    const salt = await bcrypt.genSalt();
    const hashedPassword = await bcrypt.hash(newPassword, salt);

    user.password = hashedPassword;
    user.isDeafaultPassword = false;

    await this.userRepository.save(user);

    return { message: 'Password updated successfully' };
  }

  async createSupportAdmin(
    createSupportAdminDto: CreateSupportAdminDto,
    userManager: UserItem,
  ) {
    if (!userManager || !userManager.manager) throw new UnauthorizedException();

    const { name, surname, email, password, birthDate, gender, phone } =
      createSupportAdminDto;

    const agency = userManager.manager.agency;
    if (!agency) throw new BadRequestException();

    const found = await this.userRepository
      .createQueryBuilder('user')
      .where('user.email = :email OR user.phone = :phone', {
        email,
        phone,
      })
      .getOne();

    if (found) throw new ConflictException();

    const salt = await bcrypt.genSalt();
    const hashedPassword = await bcrypt.hash(password, salt);

    const userSupportAdmin = await this.userRepository.create({
      name: name,
      surname: surname,
      email: email,
      password: hashedPassword,
      birthDate: birthDate,
      gender: gender,
      phone: phone,
      role: UserRoles.SUPPORT_ADMIN,
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
      },
    };
  }

  async createAgent(createAgentDto: CreateAgentDto) {

    const {licenseNumber, name, surname, email, password, birthDate, gender, phone,start_date,languages, agencyId} = createAgentDto;
    
    const found = await this.agentRepository.createQueryBuilder('agent')
      .where('agent.licenseNumber = :licenseNumber', { licenseNumber })
    if (found) new ConflictException(`Agent with license "${licenseNumber}" already exists`);

    const salt = await bcrypt.genSalt();
    const hashedPassword = await bcrypt.hash(password, salt);

    const userAgent = await this.userRepository.create({
      name: name,
      surname: surname,
      email: email,
      password: hashedPassword,
      phone: phone,
      gender,
      birthDate: birthDate,
      role: UserRoles.AGENT,
    });

    await this.userRepository.save(userAgent);

    const agent = this.agentRepository.create({
      licenseNumber: licenseNumber,
      start_date: start_date,
      languages: languages,
      user: userAgent, 
      agency: { id: agencyId }, 
    });

    await this.agentRepository.save(agent);

    return {
      message: 'Agent created successfully',
      agent: {
        email: userAgent.email,
        id: userAgent.id,
        name: userAgent.name,
      },
    };

  }

  async deleteAgentById(idAgente: string) {
    const found = await this.userRepository.findOneBy({ id: idAgente });
    if (!found) throw new NotFoundException(`Agent with id "${idAgente}" not found`);

    await this.userRepository.delete(idAgente); 

  }
}

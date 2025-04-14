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
import { CreateSupportAdminByAdminDto } from './dto/create-support-admin-by-admin.dto';
import { CreateSupportAdminDto } from './dto/create-support-admin.dto';

@Injectable()
export class AgencyManagerService {
  constructor(
    @InjectRepository(Manager)
    private readonly managerRepository: Repository<Manager>,

    @InjectRepository(User)
    private readonly userRepository: Repository<User>,

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

  async createAgent(createAgentDto, agencyId) {
    //controlo se esiste gia un agente che ha lo stesso numero di licenza
    //crea la tupla dell'agente nella Tabella user
    //crei la tupla nella tabella agent (userId, agency)
  }

  async deleteAgentById() {
    // eliminare la tupla della user -> cancella da sola la tupla in Agent -> Annunci immobiliari pubblicati dall'agente
  }
}

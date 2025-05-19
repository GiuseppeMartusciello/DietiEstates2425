import {
  Injectable,
  NotFoundException,
  UnauthorizedException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { User } from './user.entity';
import { Repository } from 'typeorm';
import { CredentialDto } from 'src/agency-manager/dto/credentials.dto';
import * as bcrypt from 'bcrypt';

@Injectable()
export class UserService {
  constructor(
    @InjectRepository(User)
    private readonly userRepository: Repository<User>,
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

  private async hashPassword(password: string): Promise<string> {
    const salt = await bcrypt.genSalt();
    return bcrypt.hash(password, salt);
  }
}

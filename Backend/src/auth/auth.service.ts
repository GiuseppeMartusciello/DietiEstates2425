import {
  BadRequestException,
  ConflictException,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { User } from './user.entity';
import { Repository } from 'typeorm';
import { AuthCredentialDto } from './dto/auth.credentials.dto';
import { TokensDto } from './dto/tokens.dto';
import * as bcrypt from 'bcrypt';
import { Client } from 'src/client/client.entity';
import { UserRoles } from 'src/common/types/user-roles';
import { JwtPayload } from './dto/jwt-payload.dto';
import { JwtService } from '@nestjs/jwt';
import { SignInDto } from './dto/signin.credentials.dto';
import { Gender } from 'src/common/types/gender.enum';
import { Provider } from 'src/common/types/provider.enum';
import { GoogleUser } from 'src/common/types/google-user';

@Injectable()
export class AuthService {
  constructor(
    @InjectRepository(User)
    private readonly userRepository: Repository<User>,

    @InjectRepository(Client)
    private readonly clientRepository: Repository<Client>,

    private jwtService: JwtService,
  ) {}

  async signUp(authCredentialDto: AuthCredentialDto): Promise<TokensDto> {
    const {
      name,
      surname,
      email,
      password,
      birthDate,
      gender,
      phone,
      address,
    } = authCredentialDto;

    const found = await this.userRepository
      .createQueryBuilder('user')
      .where('user.email = :email OR user.phone = :phone', {
        email,
        phone,
      })
      .getOne();

    if (found) throw new ConflictException('User already exists');

    const hashedPassword = await this.hashPassword(password);

    const user = this.userRepository.create({
      name,
      surname,
      birthDate,
      gender,
      phone,
      role: UserRoles.CLIENT,
      email,
      password: hashedPassword,
      provider: Provider.LOCAL,
    });

    await this.userRepository.save(user);

    const client = this.clientRepository.create({
      userId: user.id,
      address: address,
    });

    await this.clientRepository.save(client);

    const payload: JwtPayload = {
      userId: user.id,
      role: user.role,
    };

    const accessToken = await this.createToken(payload, '1h');

    return { accessToken };
  }

  async signIn(credentials: SignInDto): Promise<TokensDto> {
    const { email, password } = credentials;

    const user = await this.userRepository.findOne({
      where: { email },
    });

    // 1. controllo che l'utenta esista e che le credenziali siano corrette
    if (!user || !(await bcrypt.compare(password, user.password))) {
      throw new UnauthorizedException('Invalid credentials');
    }

    if (user.provider && user.provider !== Provider.LOCAL) {
      throw new BadRequestException(
        `This account is linked with ${user.provider}. Please use ${user.provider} login.`,
      );
    }

    const payload: JwtPayload = { userId: user.id, role: user.role };

    const accessToken = await this.createToken(payload, '45m');

    console.log(user);

    return { accessToken };
  }

  async socialLogin(googleUser: GoogleUser) {
    const user = await this.userRepository.findOne({
      where: { email: googleUser.email },
    });

    if (user)
      if (user.provider === Provider.GOOGLE)
        return this.createToken(
          { userId: user.id, role: UserRoles.CLIENT },
          '45m',
        );
      else
        throw new BadRequestException(
          `User registered with different provider: ${user.provider}`,
        );

    const defaultPassword = await this.hashPassword('Password1234!');
    const newUser = this.userRepository.create({
      email: googleUser.email,
      name: googleUser.name,
      surname: googleUser.surname,
      password: defaultPassword,
      birthDate: new Date('1900-01-01'),
      gender: Gender.OTHER,
      phone: '0000000000',
      role: UserRoles.CLIENT,
      isDeafaultPassword: true,
      provider: Provider.GOOGLE,
    });

    await this.userRepository.save(newUser);

    const client = this.clientRepository.create({
      userId: newUser.id,
    });

    await this.clientRepository.save(client);

    return this.createToken({ userId: newUser.id, role: newUser.role }, '45m'); // JWT
  }

  private async createToken(payload: JwtPayload, expiresIn: string) {
    return this.jwtService.sign(payload, {
      expiresIn: expiresIn,
    });
  }

  private async hashPassword(password: string) {
    const salt = await bcrypt.genSalt();
    return bcrypt.hash(password, salt);
  }
}

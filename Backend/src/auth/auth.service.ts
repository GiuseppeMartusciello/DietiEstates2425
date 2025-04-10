import {
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
import { Roles } from 'src/common/types/roles';
import { JwtPayload } from './dto/jwt-payload.dto';
import { JwtService } from '@nestjs/jwt';
import { SignInDto } from './dto/signin.credentials.dto';

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

    const found = await this.userRepository.findOne({
      where: { email: email },
    });

    if (found) throw new ConflictException('User already exists');

    const salt = await bcrypt.genSalt();
    const hashedPassword = await bcrypt.hash(password, salt);

    const user = this.userRepository.create({
      name,
      surname,
      birthDate,
      gender,
      phone,
      role: Roles.CLIENT,
      email,
      password: hashedPassword,
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

    const accessToken = await this.jwtService.sign(payload, {
      expiresIn: '1h',
    });

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

    const payload: JwtPayload = { userId: user.id, role: user.role };

    const accessToken = await this.jwtService.sign(payload, {
      expiresIn: '45m',
    });

    return { accessToken };
  }
}

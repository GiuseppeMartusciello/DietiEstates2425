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
import { AuthResponse } from './types/auth-response';
import * as bcrypt from 'bcrypt';
import { Client } from 'src/client/client.entity';
import { UserRoles } from 'src/common/types/user-roles';
import { JwtPayload } from './dto/jwt-payload.dto';
import { JwtService } from '@nestjs/jwt';
import { SignInDto } from './dto/signin.credentials.dto';
import { Gender } from 'src/common/types/gender.enum';
import { Provider } from 'src/common/types/provider.enum';
import { GoogleUser } from 'src/common/types/google-user';
import * as crypto from 'crypto';
import { OAuth2Client } from 'google-auth-library';
import { use } from 'passport';

@Injectable()
export class AuthService {
  constructor(
    @InjectRepository(User)
    private readonly userRepository: Repository<User>,

    @InjectRepository(Client)
    private readonly clientRepository: Repository<Client>,

    private jwtService: JwtService,
  ) {}

  async signUp(authCredentialDto: AuthCredentialDto): Promise<AuthResponse> {
    const {
      name,
      surname,
      email,
      password,
      phone,
      // birthDate,
      // gender,
      // address,
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
      email,
      password: hashedPassword,
      phone,
      // birthDate,
      // gender,
      role: UserRoles.CLIENT,
      provider: Provider.LOCAL,
      lastPasswordChangeAt: new Date(),
    });

    await this.userRepository.save(user);

    const client = this.clientRepository.create({
      userId: user.id,
      // address: address,
    });

    await this.clientRepository.save(client);

    const payload: JwtPayload = {
      userId: user.id,
      role: user.role,
    };

    const accessToken = await this.createToken(payload, '1h');

    return {
      accessToken: accessToken,
      mustChangePassword: false,
    };
  }

  async signIn(credentials: SignInDto): Promise<AuthResponse> {
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

    return {
      accessToken: accessToken,
      mustChangePassword: !user.lastPasswordChangeAt,
    };
  }

  async verifyGoogleTokenAndLogin(idToken: string): Promise<AuthResponse> {
    const client = new OAuth2Client(process.env.GOOGLE_CLIENT_ID);
    const ticket = await client.verifyIdToken({
      idToken,
      audience: process.env.GOOGLE_CLIENT_ID,
    });

    const payload = ticket.getPayload();

    const email = payload?.email;
    const name = payload?.given_name;
    const surname = payload?.family_name;

    if (!email) throw new UnauthorizedException('Email non trovata nel token');

    const googleUser: GoogleUser = {
      email,
      name: name || '',
      surname: surname || '',
    };

    return this.socialLogin(googleUser);
  }

  async socialLogin(googleUser: GoogleUser): Promise<AuthResponse> {
    console.log(googleUser);
    const user = await this.userRepository.findOne({
      where: { email: googleUser.email },
    });

    if (user)
      if (user.provider === Provider.GOOGLE) {
        const accessToken = await this.createToken(
          { userId: user.id, role: user.role },
          '45m',
        );
        return { accessToken, mustChangePassword: false };
      } else
        throw new BadRequestException(
          `User registered with different provider: ${user.provider}`,
        );

    const defaultPassword = await this.hashPassword(
      crypto.randomBytes(16).toString('hex'),
    );
    const newUser = this.userRepository.create({
      email: googleUser.email,
      name: googleUser.name,
      surname: googleUser.surname,
      password: defaultPassword,
      lastPasswordChangeAt: new Date(),
      role: UserRoles.CLIENT,
      provider: Provider.GOOGLE,
    });

    await this.userRepository.save(newUser);

    const client = this.clientRepository.create({
      userId: newUser.id,
    });

    await this.clientRepository.save(client);

    const accessToken = await this.createToken(
      { userId: newUser.id, role: newUser.role },
      '45m',
    );
    return { accessToken, mustChangePassword: false };
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

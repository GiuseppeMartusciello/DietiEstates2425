import { Injectable, Logger, UnauthorizedException } from '@nestjs/common';
import { ExtractJwt, Strategy } from 'passport-jwt';
import { PassportStrategy } from '@nestjs/passport';
import { ConfigService } from '@nestjs/config';
import { InjectRepository } from '@nestjs/typeorm';
import { User } from './user.entity';
import { Repository } from 'typeorm';
import { JwtPayload } from './dto/jwt-payload.dto';
import { Manager } from 'src/agency-manager/agency-manager.entity';
import { Agent } from 'src/agent/agent.entity';
import { SupportAdmin } from 'src/support-admin/support-admin.entity';
import { Client } from 'src/client/client.entity';
import { UserRoles } from 'src/common/types/user-roles';
import { UserItem } from 'src/common/types/userItem';

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy) {
  constructor(
    @InjectRepository(User)
    private readonly userRepository: Repository<User>,

    @InjectRepository(Client)
    private readonly clientRepository: Repository<Client>,

    @InjectRepository(Manager)
    private readonly managerRepository: Repository<Manager>,

    @InjectRepository(Agent)
    private readonly agentRepository: Repository<Agent>,

    @InjectRepository(SupportAdmin)
    private readonly supportAdminRepository: Repository<SupportAdmin>,

    private readonly configService: ConfigService,
  ) {
    super({
      secretOrKey: configService.get<string>('JWT_SECRET', 'defaultSecret'),
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
    });
  }

  async validate(payload: JwtPayload): Promise<UserItem> {
    const { userId } = payload;

    const user = await this.userRepository.findOne({
      where: { id: userId },
    });

    if (!user) throw new UnauthorizedException('Unauthorized');

    const userItem = user as UserItem;

    if (user.role === UserRoles.CLIENT) {
      const client = await this.clientRepository.findOne({
        where: { userId: user.id },
      });

      if (!client) throw new UnauthorizedException('Unathorized');

      userItem.client = client;
    } else if (user.role === UserRoles.MANAGER) {
      const manager = await this.managerRepository.findOne({
        where: { userId: user.id },
      });

      if (!manager) throw new UnauthorizedException('Unathorized');

      userItem.manager = manager;
    } else if (user.role === UserRoles.AGENT) {
      const agent = await this.agentRepository.findOne({
        where: { userId: user.id },
      });

      if (!agent) throw new UnauthorizedException('Unathorized');

      userItem.agent = agent;
    } else {
      const supportAdmin = await this.supportAdminRepository.findOne({
        where: { userId: user.id },
      });

      if (!supportAdmin) throw new UnauthorizedException('Unathorized');

      userItem.supportAdmin = supportAdmin;
    }


    return userItem;
  }
}

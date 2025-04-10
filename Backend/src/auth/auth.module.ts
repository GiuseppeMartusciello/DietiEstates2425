import { Module } from '@nestjs/common';
import { AuthController } from './auth.controller';
import { TypeOrmModule } from '@nestjs/typeorm';
import { User } from './user.entity';
import { Client } from 'src/client/client.entity';
import { PropertyOffer } from 'src/property_offer/property_offer.entity';
import { Agency } from 'src/agency/agency.entity';
import { Agent } from 'src/agent/agent.entity';
import { Manager } from 'src/agency-manager/agency-manager.entity';
import { Notification } from 'src/notification/notification.entity';
import { Research } from 'src/research/research.entity';
import { Listing } from 'src/listing/Listing.entity';
import { SupportAdmin } from 'src/support-admin/support-admin.entity';
import { AuthService } from './auth.service';
import { JwtStrategy } from './jwt.strategy';
import { PassportModule } from '@nestjs/passport';
import { JwtModule } from '@nestjs/jwt';
import { ConfigModule, ConfigService } from '@nestjs/config';

@Module({
  imports: [
    ConfigModule,
    TypeOrmModule.forFeature([
      User,
      Client,
      PropertyOffer,
      Agency,
      Agent,
      Manager,
      Notification,
      Research,
      Listing,
      SupportAdmin,
    ]),
    PassportModule.register({ defaultStrategy: 'jwt' }),
    JwtModule.registerAsync({
      imports: [ConfigModule],
      inject: [ConfigService],
      useFactory: async (configService: ConfigService) => ({
        secret: configService.get('JWT_SECRET'),
        signOptions: {
          expiresIn: 3600,
        },
      }),
    }),
  ],
  controllers: [AuthController],
  providers: [AuthService, JwtStrategy],
  exports: [JwtStrategy, PassportModule],
})
export class AuthModule {}

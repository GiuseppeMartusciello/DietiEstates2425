import { Module } from '@nestjs/common';
import { AuthController } from './auth.controller';
import { TypeOrmModule } from '@nestjs/typeorm';
import { User } from './user.entity';
import { Client } from 'src/client.entity';
import { PropertyOffer } from 'src/property_offer.entity';
import { Agency } from 'src/agency.entity';
import { Agent } from 'src/agent.entity';
import { Manager } from 'src/agency-manager.entity';
import { Notification } from 'src/notification.entity';
import { Research } from 'src/research.entity';
import { Listing } from 'src/Listing.entity';
import { SupportAdmin } from 'src/support-admin.entity';
import { AuthService } from './auth.service';

@Module({
  imports: [
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
  ],
  controllers: [AuthController],
  providers: [AuthService],
})
export class AuthModule {}

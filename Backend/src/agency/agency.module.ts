import { Module } from '@nestjs/common';
import { AgencyController } from './agency.controller';
import { AgencyService } from './agency.service';
import { ConfigService } from '@nestjs/config';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Manager } from 'src/agency-manager/agency-manager.entity';
import { User } from 'src/auth/user.entity';
import { SupportAdmin } from 'src/support-admin/support-admin.entity';
import { Agency } from './agency.entity';
import { Agent } from '../agent/agent.entity';

@Module({
  controllers: [AgencyController],
  providers: [AgencyService, ConfigService],
  imports: [
    TypeOrmModule.forFeature([Manager, User, SupportAdmin, Agency, Agent]),
  ],
})
export class AgencyModule {}

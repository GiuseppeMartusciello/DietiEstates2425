import { Module } from '@nestjs/common';
import { AgencyManagerController } from './agency-manager.controller';
import { AgencyManagerService } from './agency-manager.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Manager } from './agency-manager.entity';
import { SupportAdmin } from 'src/support-admin/support-admin.entity';
import { User } from 'src/auth/user.entity';
import { Agency } from 'src/agency/agency.entity';
import { Agent } from 'src/agent/agent.entity';

@Module({
  controllers: [AgencyManagerController],
  providers: [AgencyManagerService],
  imports: [
    TypeOrmModule.forFeature([Manager, User, SupportAdmin, Agency, Agent]),
  ],
})
export class AgencyManagerModule {}

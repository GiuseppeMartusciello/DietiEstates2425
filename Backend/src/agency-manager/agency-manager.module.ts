import { Module } from '@nestjs/common';
import { AgencyManagerController } from './agency-manager.controller';
import { AgencyManagerService } from './agency-manager.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Manager } from './agency-manager.entity';

@Module({
  controllers: [AgencyManagerController],
  providers: [AgencyManagerService],
  imports: [TypeOrmModule.forFeature([Manager])],
})
export class AgencyManagerModule {}

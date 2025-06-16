import { Module } from '@nestjs/common';
import { ListingController } from './listing.controller';
import { ListingService } from './listing.service';
import { AuthModule } from 'src/auth/auth.module';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ListingRepository } from './listing.repository';
import { Listing } from './Listing.entity';
import { AgentModule } from 'src/agent/agent.module';
import { GeoapifyService } from 'src/common/services/geopify.service';
import { NotificationService } from 'src/notification/notification.service';
import { NotificationModule } from 'src/notification/notification.module';

@Module({
  imports: [TypeOrmModule.forFeature([Listing]), AuthModule, AgentModule,NotificationModule],
  controllers: [ListingController],
  providers: [ListingService, ListingRepository, GeoapifyService],
  exports: [ListingService, ListingRepository],
})
export class ListingModule {}

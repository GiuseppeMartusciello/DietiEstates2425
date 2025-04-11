import { Module } from '@nestjs/common';
import { ListingController } from './listing.controller';
import { ListingService } from './listing.service';
import { AuthModule } from 'src/auth/auth.module';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ListingRepository } from './listing.repository';
import { Listing } from './Listing.entity';
import { AgentModule } from 'src/agent/agent.module';
import { GeoapifyService } from 'src/common/services/geopify.service';

@Module({
  imports: [TypeOrmModule.forFeature([Listing]), AuthModule, AgentModule],
  controllers: [ListingController],
  providers: [ListingService, ListingRepository, GeoapifyService],
})
export class ListingModule {}

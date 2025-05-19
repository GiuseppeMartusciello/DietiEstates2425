import { Module } from '@nestjs/common';
import { ResearchController } from './research.controller';
import { ResearchService } from './research.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Research } from './research.entity';
import { AuthModule } from 'src/auth/auth.module';
import { ClientModule } from 'src/client/client.module';
import { Listing } from 'src/listing/Listing.entity';
import { GeoapifyService } from 'src/common/services/geopify.service';
import { ListingRepository } from 'src/listing/listing.repository';


@Module({
  imports: [TypeOrmModule.forFeature([Research,Listing]), AuthModule, ClientModule],
  controllers: [ResearchController],
  providers: [ResearchService,ListingRepository,GeoapifyService],
  exports: [],
})
export class ResearchModule {}

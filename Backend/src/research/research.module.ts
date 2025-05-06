import { Module } from '@nestjs/common';
import { ResearchController } from './research.controller';
import { ResearchService } from './research.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Research } from './research.entity';
import { AuthModule } from 'src/auth/auth.module';
import { ResearchRepository } from './research.repository';
import { ClientModule } from 'src/client/client.module';
import { Listing } from 'src/listing/Listing.entity';

@Module({
  imports: [TypeOrmModule.forFeature([Research,Listing]), AuthModule, ClientModule],
  controllers: [ResearchController],
  providers: [ResearchService, ResearchRepository],
  exports: [ResearchRepository],
})
export class ResearchModule {}

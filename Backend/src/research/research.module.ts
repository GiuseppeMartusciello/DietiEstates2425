import { Module } from '@nestjs/common';
import { ResearchController } from './research.controller';
import { ResearchService } from './research.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Research } from './research.entity';

@Module({
  imports: [TypeOrmModule.forFeature([Research])],
  controllers: [ResearchController],
  providers: [ResearchService],
})
export class ResearchModule {}

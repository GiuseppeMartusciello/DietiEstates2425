import { Module } from '@nestjs/common';
import { ResearchController } from './research.controller';
import { ResearchService } from './research.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Research } from './research.entity';
import { AuthModule } from 'src/auth/auth.module';
import { ResearchRepository } from './research.repository';
import { ClientModule } from 'src/client/client.module';


@Module({
  imports: [TypeOrmModule.forFeature([Research]), AuthModule, ClientModule],
  controllers: [ResearchController],
  providers: [ResearchService, ResearchRepository],
  exports: [ResearchRepository],
})
export class ResearchModule {}

import { Module } from '@nestjs/common';
import { AgentService } from './agent.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Agent } from './agent.entity';
import { AgentController } from './agent.controller';

@Module({
  imports: [TypeOrmModule.forFeature([Agent])],
  providers: [AgentService],
  exports: [AgentService],
  controllers: [AgentController],
})
export class AgentModule {}

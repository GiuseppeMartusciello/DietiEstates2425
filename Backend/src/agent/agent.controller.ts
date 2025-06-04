import {
  Controller,
  Get,
  Param,
  ParseUUIDPipe,
  UseGuards,
} from '@nestjs/common';
import { AgentService } from './agent.service';
import { Agent } from './agent.entity';
import { RolesGuard } from 'src/common/guards/roles.guard';
import { AuthGuard } from '@nestjs/passport';

@Controller('agent')
@UseGuards(AuthGuard('jwt'), RolesGuard)
export class AgentController {
  constructor(private readonly agentService: AgentService) {}

  @Get('/:id')
  getAgentById(@Param('id', new ParseUUIDPipe()) id: string): Promise<Agent> {
    return this.agentService.getAgentById(id);
  }
}

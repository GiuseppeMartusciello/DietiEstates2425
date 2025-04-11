import { Controller, Get, Param, ParseUUIDPipe, UseGuards } from '@nestjs/common';
import { AgentService } from './agent.service';
import { UserRoles } from 'src/common/types/user-roles';
import { Roles } from 'src/common/decorator/roles.decorator';
import { Agent } from './agent.entity';
import { RolesGuard } from 'src/common/guards/roles.guard';
import { AuthGuard } from '@nestjs/passport';

@Controller('agent')
@UseGuards(AuthGuard('jwt'), RolesGuard)
export class AgentController {
  constructor(private agentService: AgentService) {}

  @Get('/:id')
  @Roles(UserRoles.MANAGER, UserRoles.SUPPORT_ADMIN)
  getClientById(@Param('id', new ParseUUIDPipe()) id: string): Promise<Agent> {
    return this.agentService.getAgentById(id);
  }
}

import {
  Body,
  Controller,
  Delete,
  Param,
  ParseUUIDPipe,
  Patch,
  Post,
  UnauthorizedException,
  UseGuards,
} from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';
import { RolesGuard } from 'src/common/guards/roles.guard';
import { AgencyManagerService } from './agency-manager.service';
import { CredentialDto } from './dto/credentials.dto';
import { GetUser } from 'src/auth/get-user.decorator';
import { UserItem } from 'src/common/types/userItem';
import { Roles } from 'src/common/decorator/roles.decorator';
import { UserRoles } from 'src/common/types/user-roles';
import { CreateSupportAdminDto } from './dto/create-support-admin.dto';
import { CreateAgentDto } from './dto/create-agent.dto';

@Controller('agency-manager')
@UseGuards(AuthGuard('jwt'), RolesGuard)
export class AgencyManagerController {
  constructor(private readonly managerService: AgencyManagerService) {}

  @Post('support-admin')
  @Roles(UserRoles.MANAGER)
  createSupportAdmin(
    @Body() createSupportAdminDto: CreateSupportAdminDto,
    @GetUser() user: UserItem,
  ) {
    return this.managerService.createSupportAdmin(createSupportAdminDto, user);
  }

  //inserire un nuovo agente (admin, support-admin)
  @Post('agent')
  @Roles(UserRoles.MANAGER, UserRoles.SUPPORT_ADMIN)
  createAgent(
    @GetUser() user: UserItem,
    @Body() createAgentDto: CreateAgentDto,
  ) {
    const agencyId = user.manager?.agency?.id || user.supportAdmin?.agency?.id;
    if (!agencyId) throw new UnauthorizedException('No agency associated');

    return this.managerService.createAgent(createAgentDto, agencyId);
  }

  //eliminare un agente
  @Delete('agent/:id/delete')
  @Roles(UserRoles.MANAGER, UserRoles.SUPPORT_ADMIN)
  deleteAgent(@Param('id', new ParseUUIDPipe()) agentId: string) {
    return this.managerService.deleteAgentById(agentId);
  }
}

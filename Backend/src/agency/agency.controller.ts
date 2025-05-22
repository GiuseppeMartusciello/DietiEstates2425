import {
  BadRequestException,
  Body,
  Controller,
  Delete,
  Get,
  Param,
  ParseUUIDPipe,
  Patch,
  Post,
  UnauthorizedException,
  UseGuards,
} from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';
import { RolesGuard } from 'src/common/guards/roles.guard';
import { AgencyService } from './agency.service';
import { GetUser } from 'src/auth/get-user.decorator';
import { UserItem } from 'src/common/types/userItem';
import { Roles } from 'src/common/decorator/roles.decorator';
import { UserRoles } from 'src/common/types/user-roles';
import { CreateSupportAdminDto } from '../agency-manager/dto/create-support-admin.dto';
import { CreateAgentDto } from '../agency-manager/dto/create-agent.dto';

@Controller('agency')
@UseGuards(AuthGuard('jwt'), RolesGuard)
export class AgencyController {
  constructor(private readonly agencyService: AgencyService) {}

  @Post('/support-admin')
  @Roles(UserRoles.MANAGER)
  createSupportAdmin(
    @Body() createSupportAdminDto: CreateSupportAdminDto,
    @GetUser() user: UserItem,
  ) {
    return this.agencyService.createSupportAdmin(createSupportAdminDto, user);
  }

  //inserire un nuovo agente (admin, support-admin)
  @Post('/agent')
  @Roles(UserRoles.MANAGER, UserRoles.SUPPORT_ADMIN)
  createAgent(
    @GetUser() user: UserItem,
    @Body() createAgentDto: CreateAgentDto,
  ) {
    const agencyId = user.manager?.agency?.id || user.supportAdmin?.agency?.id;
    if (!agencyId) throw new UnauthorizedException('No agency associated');

    return this.agencyService.createAgent(createAgentDto, agencyId);
  }

  //eliminare un agente
  @Delete('/agent/:id/delete')
  @Roles(UserRoles.MANAGER, UserRoles.SUPPORT_ADMIN)
  deleteAgent(
    @GetUser() user: UserItem,
    @Param('id', new ParseUUIDPipe()) agentId: string,
  ) {
    const agencyId = user.manager?.agency?.id || user.supportAdmin?.agency?.id;
    if (!agencyId) 
      throw new BadRequestException('Nessuna agenzia associata all’utente.');

    return this.agencyService.deleteAgentById(agentId, agencyId);
  }

  @Delete('/support-admin/:id/delete')
  @Roles(UserRoles.MANAGER)
  deleteSupportAdmin(
    @GetUser() user: UserItem,
    @Param('id', new ParseUUIDPipe()) supportAdminId: string,
  ) {
    const agencyId = user.manager?.agency.id;
    if (!agencyId) {
      throw new BadRequestException('Nessuna agenzia associata all’utente.');
    }

    return this.agencyService.deleteSupportAdminById(supportAdminId, agencyId);
  }

  @Get('/agents')
  @Roles(UserRoles.MANAGER, UserRoles.SUPPORT_ADMIN)
  getAgents(@GetUser() user: UserItem) {
    const agencyId = user.manager?.agency?.id || user.supportAdmin?.agency?.id;
    if (!agencyId) {
      throw new BadRequestException('Nessuna agenzia associata all’utente.');
    }
    return this.agencyService.getAgents(agencyId);
  }
}

import {
  BadRequestException,
  Body,
  Controller,
  Delete,
  Get,
  NotFoundException,
  Param,
  ParseUUIDPipe,
  Post,
  UnauthorizedException,
  UseGuards,
} from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';
import { RolesGuard } from 'src/common/guards/roles.guard';
import { ListingService } from './listing.service';
import { UserRoles } from 'src/common/types/user-roles';
import { Roles } from 'src/common/decorator/roles.decorator';
import { GetUser } from 'src/auth/get-user.decorator';
import { CreateListingDto } from './dto/create-listing.dto';
import { UserItem } from 'src/common/types/userItem';
import { Listing } from './Listing.entity';
import { AgentService } from 'src/agent/agent.service';

@Controller('listing')
@UseGuards(AuthGuard('jwt'), RolesGuard)
export class ListingController {
  constructor(
    private readonly listingService: ListingService,
    private readonly agentService: AgentService,
  ) {}

  @Delete('/:id')
  @Roles(UserRoles.AGENT, UserRoles.SUPPORT_ADMIN, UserRoles.MANAGER)
  async deleteListing(
    @Param('id', new ParseUUIDPipe()) id: string,
    @GetUser() user: UserItem,
  ): Promise<void> {
    const listing: Listing = await this.getListingById(id);
    if (!listing)
      throw new NotFoundException(`Listing with id ${id} not found `);

    if (user.agent && listing.agent.userId !== user.id)
      throw new UnauthorizedException();

    this.listingService.deleteListingById(id);
  }

  @Get('/agency/:id')
  getListingByAgencyId(
    @Param('id', new ParseUUIDPipe()) id: string,
  ): Promise<Listing[]> {
    return this.listingService.getListingByAgencyId(id);
  }

  @Get('/:id')
  getListingById(
    @Param('id', new ParseUUIDPipe()) id: string,
  ): Promise<Listing> {
    return this.listingService.getListingById(id);
  }

  @Post()
  @Roles(UserRoles.AGENT, UserRoles.SUPPORT_ADMIN, UserRoles.MANAGER)
  async createListing(
    @Body() createListingDto: CreateListingDto,
    @GetUser() user: UserItem,
  ): Promise<Listing> {
    //se l'utente è un agente lo uso direttamente
    if (user.agent)
      return this.listingService.createListing(createListingDto, user.agent);

    //Se non è un agente devo avere l'id del referente
    const agentId = createListingDto.agentId;
    if (!agentId)
      throw new BadRequestException('Missing agentId in request body');

    const agent = await this.agentService.getAgentById(
      createListingDto.agentId,
    );
    if (!agent)
      throw new NotFoundException(`Agent with userId ${agentId} not found `);

    return this.listingService.createListing(createListingDto, agent);
  }
}

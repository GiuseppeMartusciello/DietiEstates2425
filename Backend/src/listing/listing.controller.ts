import {
  BadRequestException,
  Body,
  Controller,
  Delete,
  Get,
  NotFoundException,
  Param,
  ParseUUIDPipe,
  Patch,
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
import { ModifyListingDto } from './dto/modify-listing.dto';
import { SearchListingDto } from './dto/search-listing.dto';
import { Agent } from 'src/agent/agent.entity';

@Controller('listing')
@UseGuards(AuthGuard('jwt'), RolesGuard)
export class ListingController {
  constructor(
    private readonly listingService: ListingService,
    private readonly agentService: AgentService,
  ) {}

  @Get('')
  @Roles(UserRoles.AGENT, UserRoles.SUPPORT_ADMIN, UserRoles.MANAGER)
  getAllListing(): Promise<Listing[]> {
    return this.listingService.getAllListing();
  }

  @Get('/agent/:id')
  @Roles(UserRoles.AGENT, UserRoles.SUPPORT_ADMIN, UserRoles.MANAGER)
  async getListingByAgentId(
    @Param('id', new ParseUUIDPipe()) agentId: string,
    @GetUser() user: UserItem,
  ): Promise<Listing[]> {
    if (user.agent)
      if (user.agent?.userId !== agentId)
        //se la richiesta è effettuata da un agente il suo id deve corrispondere con la richiesta
        throw new UnauthorizedException();
      else return this.listingService.getListingByAgentId(user.id);
    else {
      const agent: Agent = await this.agentService.getAgentById(agentId);
      if (!agent)
        throw new NotFoundException(`Agent with userId ${agentId} not found `);

      //se la richiesta è effettuata da un support_admin/manager la sua agenzia deve corrispondere con quella del agent richiesto
      if (
        user.supportAdmin?.agency != agent.agency &&
        user.manager?.agency != agent.agency
      )
        throw new UnauthorizedException();

      return this.listingService.getListingByAgentId(user.id);
    }
  }

  @Get('/agency/:id')
  @Roles(UserRoles.SUPPORT_ADMIN, UserRoles.MANAGER)
  getListingByAgencyId(
    @Param('id', new ParseUUIDPipe()) id: string,
    @GetUser() user: UserItem,
  ): Promise<Listing[]> {
    if (user.supportAdmin?.agency.id !== id && user.manager?.agency.id !== id)
      throw new UnauthorizedException();

    return this.listingService.getListingByAgencyId(id);
  }

  @Get('/:id')
  getListingById(
    @Param('id', new ParseUUIDPipe()) id: string,
  ): Promise<Listing> {
    return this.listingService.getListingById(id);
  }

  @Post('/search') //Dovrebbe essere una Get, ma avendo un DTO complesso si utilizza la post lo stesso
  searchListing(
    @Body() searchListingDto: SearchListingDto,
  ): Promise<Listing[]> {
    return this.listingService.searchListing(searchListingDto);
  }

  @Patch('')
  @Roles(UserRoles.AGENT, UserRoles.SUPPORT_ADMIN, UserRoles.MANAGER)
  async modifyListing(
    @Body() modifyListingDto: ModifyListingDto,
    @GetUser() user: UserItem,
  ): Promise<Listing> {
    const listing: Listing = await this.listingService.getListingById(
      modifyListingDto.listingId,
    );

    if (!listing)
      throw new NotFoundException(
        `Listing with id ${modifyListingDto.listingId} not found `,
      );

    if (user.agent && listing.agent.userId !== user.id)
      throw new UnauthorizedException();

    //aggiungere controllo che siano della stessa agenzia

    return this.listingService.changeListing(listing, modifyListingDto);
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

    return this.listingService.deleteListingById(id);
  }
}

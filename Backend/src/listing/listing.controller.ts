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

  @Get('/agency')
  @Roles(UserRoles.SUPPORT_ADMIN, UserRoles.MANAGER)
  getListingByAgencyId(@GetUser() user: UserItem): Promise<Listing[]> {
    const agencyId = this.getAgencyIdFromUser(user);

    return this.listingService.getListingByAgencyId(agencyId);
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

    this.checkAuthorization(user, listing);

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
    const agencyId = this.getAgencyIdFromUser(user);
    if (user.agent)
      return this.listingService.deleteListingById(id, agencyId, user.id);

    return this.listingService.deleteListingById(id, agencyId);
  }

  checkAuthorization(user: UserItem, listing: Listing): void {
    if (user.agent && user.agent.userId != listing.agent.userId)
      throw new UnauthorizedException();

    if (user.supportAdmin && user.supportAdmin.agency !== listing.agency)
      throw new UnauthorizedException();

    if (user.manager && user.manager.agency !== listing.agency)
      throw new UnauthorizedException();
  }

  getAgencyIdFromUser(user: UserItem): string {
    const agencyId =
      user.agent?.agency.id ||
      user.manager?.agency.id ||
      user.supportAdmin?.agency.id;
    if (!agencyId) throw new UnauthorizedException();

    return agencyId;
  }
}

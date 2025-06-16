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
  UploadedFiles,
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
import { ResearchListingDto } from '../research/dto/create-research.dto';
import { ListingImageUploadInterceptor } from 'src/common/interceptors/listing-image-upload.interceptor';
import { ListingResponse } from './dto/listing-with-image.dto';

@Controller('listing')
@UseGuards(AuthGuard('jwt'), RolesGuard)
export class ListingController {
  constructor(
    private readonly listingService: ListingService,
    private readonly agentService: AgentService,
  ) {}
  @Get('/all-images')
  async getAllListingImages(): Promise<Record<string, string[]>> {
    return this.listingService.getAllListingImages();
  }

  @Get('/:id')
  getListingById(
    @Param('id', new ParseUUIDPipe()) id: string,
  ): Promise<ListingResponse> {
    return this.listingService.getListingById(id);
  }

  @Get()
  getAllListing(@GetUser() user: UserItem): Promise<ListingResponse[]> {
    if (user.client) return this.listingService.getAllListing();
    else if (user.agent)
      return this.listingService.getListingByAgentId(
        user.agent.userId,
        user.agent.agency.id,
      );
    else {
      const agencyId = this.getAgencyIdFromUser(user);
      return this.listingService.getListingByAgencyId(agencyId);
    }
  }

  @Post('/search')
  @Roles(UserRoles.CLIENT)
  searchListing(
    @Body() searchListingDto: ResearchListingDto,
  ): Promise<ListingResponse[]> {
    return this.listingService.searchListing(searchListingDto);
  }

  @Patch('/:id')
  @Roles(UserRoles.AGENT, UserRoles.SUPPORT_ADMIN, UserRoles.MANAGER)
  async modifyListing(
    @Param('id', new ParseUUIDPipe()) listingId: string,
    @Body() modifyListingDto: ModifyListingDto,
    @GetUser() user: UserItem,
  ): Promise<Listing> {
    const listing: Listing = await this.findListingOrThrow(listingId);
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
      throw new BadRequestException(`Agent with userId ${agentId} not found `);

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

  async findListingOrThrow(listingId: string): Promise<Listing> {
    const listing = await this.listingService.getListingForCheck(listingId);

    if (!listing)
      throw new BadRequestException(`Listing with id ${listingId} not found `);

    return listing;
  }

  checkAuthorization(user: UserItem, listing: Listing): void {
    if (user.agent && user.agent.userId != listing.agent.userId)
      throw new UnauthorizedException();

    if (user.supportAdmin && user.supportAdmin.agency.id != listing.agency.id)
      throw new UnauthorizedException();
    if (user.manager && user.manager.agency.id != listing.agency.id)
      throw new UnauthorizedException();
  }

  getAgencyIdFromUser(user: UserItem): string {
    const agencyId =
      user.agent?.agency.id ??
      user.manager?.agency.id ??
      user.supportAdmin?.agency.id;
    if (!agencyId) throw new UnauthorizedException();

    return agencyId;
  }

  @Post('/:id/images')
  @Roles(UserRoles.AGENT, UserRoles.SUPPORT_ADMIN, UserRoles.MANAGER)
  @ListingImageUploadInterceptor()
  async uploadImages(
    @Param('id', new ParseUUIDPipe()) listingId: string,
    @GetUser() user: UserItem,
    @UploadedFiles() files: Express.Multer.File[],
  ) {
    const listing: Listing = await this.findListingOrThrow(listingId);
    this.checkAuthorization(user, listing);

    return this.listingService.handleUploadedImages(listingId, files);
  }

  @Get('/:id/agent')
  async getAgentOfListing(
    @Param('id', new ParseUUIDPipe()) listingId: string,
    @GetUser() user: UserItem,
  ) {
    return this.listingService.getAgentOfListing(listingId);
  }
  
  @Get('/:id/images')
  async getListingImages(
    @Param('id', new ParseUUIDPipe()) listingId: string,
    @GetUser() user: UserItem,
  ) {
    if (!user.client) {
      const listing: Listing = await this.findListingOrThrow(listingId);
      this.checkAuthorization(user, listing);
    }

    return this.listingService.getImagesForListing(listingId);
  }

  @Delete('/:id/images/:filename')
  @Roles(UserRoles.AGENT, UserRoles.SUPPORT_ADMIN, UserRoles.MANAGER)
  async deleteImage(
    @Param('id', new ParseUUIDPipe()) listingId: string,
    @GetUser() user: UserItem,
    @Param('filename') filename: string,
  ) {
    const listing: Listing = await this.findListingOrThrow(listingId);
    this.checkAuthorization(user, listing);

    return this.listingService.deleteListingImage(listingId, filename);
  }
}

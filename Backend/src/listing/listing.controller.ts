import { Body, Controller, NotFoundException, Post, UnauthorizedException, UseGuards } from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';
import { RolesGuard } from 'src/common/guards/roles.guard';
import { ListingService } from './listing.service';
import { UserRoles } from 'src/common/types/user-roles';
import { Roles } from 'src/common/decorator/roles.decorator';
import { GetUser } from 'src/auth/get-user.decorator';
import { CreateListingDto } from './dto/create-listing.dto';
import { UserItem } from 'src/common/types/userItem';
import { Listing } from './Listing.entity';
import { ClientService } from 'src/client/client.service';
import { AgentService } from 'src/agent/agent.service';

@Controller('listing')
@UseGuards(AuthGuard('jwt'),RolesGuard)
export class ListingController {
    constructor(
        private readonly listingService: ListingService,
        private readonly agentService: AgentService,
    ){}

    @Post()
    @Roles(UserRoles.AGENT,UserRoles.SUPPORT_ADMIN,UserRoles.MANAGER)
    async awaitcreateListing(
        @Body() createListingDto: CreateListingDto,
        @GetUser() user: UserItem,
    ): Promise<Listing> {
        if(user.agent)
            return this.listingService.createListing(createListingDto,user.agent);

        const agentId= createListingDto.agentId;
        if(!agentId)
            throw new NotFoundException(`Agent with userId ${agentId} not found `); 

        const agent = await this.agentService.getAgentById(agentId); //Mettere un controllo anche su questo risultato(?)
        
        return this.listingService.createListing(createListingDto,agent);
    }






}

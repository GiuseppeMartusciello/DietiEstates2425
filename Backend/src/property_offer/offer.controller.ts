import { Controller, Get, Post,Body, Param, ParseUUIDPipe, Patch, UseGuards } from '@nestjs/common';
import { OfferService } from './offer.service';
import { CreateOfferDto } from './dto/create-offer.dto';
import { UpdateOfferDto } from './dto/update-offer.dto';
import { Roles } from 'src/common/decorator/roles.decorator';
import { UserRoles } from 'src/common/types/user-roles';
import { PropertyOffer } from './property_offer.entity';
import { RolesGuard } from 'src/common/guards/roles.guard';
import { AuthGuard } from '@nestjs/passport';
import { UserItem } from 'src/common/types/userItem';
import { GetUser } from 'src/auth/get-user.decorator';
import { Listing } from 'src/listing/Listing.entity';
import { Client } from 'src/client/client.entity';
import { CreateExternalOfferDto } from './dto/create-externalOffer.dto';
@Controller('offer')
@UseGuards(AuthGuard('jwt'), RolesGuard)
export class OfferController {
    constructor(
        private readonly offerService: OfferService,
    ) {}
 
    // questo restituisce tutte le offerte fatte da un cliente per uno specifico immobile
    @Get('/listing/:listingId')
    @Roles(UserRoles.CLIENT)
    getallOffersByListingId(
        @Param('listingId', new ParseUUIDPipe()) listingId: string,
        @GetUser() user: UserItem,
    ): Promise<PropertyOffer[]> {

        return this.offerService.getAllOffersByListingId(listingId, user.id);
    }

    //questo metodo restituisce tutti i listining per cui il cliente ha fatto un offerta
    @Get('/listing-client')
    @Roles(UserRoles.CLIENT)
    getOffersbyClientId(
        @GetUser() user: UserItem,
    ): Promise<Listing[]> {
       return this.offerService.getListingOffersByClientId(user.id)
    }


    // si vuole gestire delle specie di chat
    // quindi serve anche un metodo che prenda tutti gli utenti che hanno fatto un offerta
    // per un  immobile gestito dal manager o dall agente
    // ho immaginato la cosa del tipo l agente clicca sull immobile e vede gli utenti che hanno fatto un offerta
    //questo è il caso in cui l agente va nella sezione 'chat'

    //?restituire un oggetto del tipo immobile e un altro campo che contiene client?
    @Get('/listing/:listingId')
    @Roles(UserRoles.AGENT, UserRoles.MANAGER, UserRoles.SUPPORT_ADMIN)
    async getClientsListiningId(
        @GetUser() agent: UserItem,
        @Param('listingId', new ParseUUIDPipe()) listingId: string,
    ): Promise<Client[]> {

        return this.offerService.getClientsByListinigId(listingId,agent);
    }

    // in questo caso l agente clicca su un cliente e vede tutte le offerte che ha fatto
    // serve sia l id dell utente sia l id della proprieta
    //questo è il caso in cui l agente clicca su una chat e vede lo storico di offerte con un cliente
    @Get('/listing/:listingId/client/:clientId')
    @Roles(UserRoles.AGENT, UserRoles.MANAGER, UserRoles.SUPPORT_ADMIN)
    async getOffersByAgentId(
        @Param('listingId', new ParseUUIDPipe()) listingId: string,
        @Param('clientId', new ParseUUIDPipe()) clientId: string,
        @GetUser() agent: UserItem,
    ): Promise<PropertyOffer[]> {

        return this.offerService.getOffersByAgentId(listingId, clientId,agent);
    }


    //è pensato solo per poter modificare lo stato di una offerta
    @Patch('/:id')
    @Roles()
    updateOffer(
        @Body() updateOfferdto: UpdateOfferDto,
        @GetUser() user: UserItem,
        @Param('id', new ParseUUIDPipe()) offerId: string,
    ): Promise<PropertyOffer> {
    
        return this.offerService.updateOffer(offerId,updateOfferdto,user);
    }


    //il post avviene sempre cliccando su un immobile
    // quindi l id dell immobile è sempre presente


    // questo post è pensato solo per il cliente
    // poiche per gli altri ruoli è necessario anche l id dell cliente come parametro
    // non si puo fare solo un unico post
    @Post('/listing/:id')
    @Roles(UserRoles.CLIENT)
    createOffer(
        @Body() offerData: CreateOfferDto,
        @Param('id', new ParseUUIDPipe()) listingId: string,
        @GetUser() user: UserItem,
    ): Promise<PropertyOffer> {
        return this.offerService.createOffer(offerData, listingId, user);
    }

    //questo è il post per l agente
    @Post('/listing/:id/client/:clientId')
    @Roles(UserRoles.AGENT, UserRoles.MANAGER, UserRoles.SUPPORT_ADMIN)
    createOfferByAgent(
        @Body() offerData: CreateOfferDto,
        @Param('id', new ParseUUIDPipe()) listingId: string,
        @Param('clientId', new ParseUUIDPipe()) clientId: string,
        @GetUser() user: UserItem,
    ): Promise<PropertyOffer> {
        return this.offerService.createOfferbyAgent(offerData,listingId,user,clientId);
    }

    // UN OFFERTA ESTERNA DALLA PIATTAFORMA  ok insert per un cliente esterno alla piattafrorma
    @Post('/listing/:id/external')
    @Roles(UserRoles.SUPPORT_ADMIN, UserRoles.MANAGER)
    createExternalOffer(
        @GetUser() admin: UserItem,
        @Body() dto: CreateExternalOfferDto
        @Param('id', new ParseUUIDPipe()) listingId: string,
    ): Promise<PropertyOffer> {
         return this.offerService.createExternalOffer(dto, admin,listingId);
    }

    
}

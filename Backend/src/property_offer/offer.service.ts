import { BadRequestException, Injectable, NotFoundException, UnauthorizedException } from "@nestjs/common";
import { OfferRepository } from "./offer.repository";
import { OfferState } from "src/common/types/offer-state";
import { Listing } from "src/listing/Listing.entity";
import { Client } from "src/client/client.entity";
import { PropertyOffer } from "./property_offer.entity";
import { CreateOfferDto } from "./dto/create-offer.dto";
import { UserItem } from "src/common/types/userItem";
import { ListingRepository } from "src/listing/listing.repository";
import { UpdateOfferDto } from "./dto/update-offer.dto";
import { UserRoles } from "src/common/types/user-roles";
import { CreateExternalOfferDto } from "./dto/create-externalOffer.dto";
import { User } from "src/auth/user.entity";
import { ClientRepository } from "src/client/client.repository";

@Injectable()
export class OfferService {
  
    constructor(
        private readonly offerRepository: OfferRepository,
        private readonly listingRepository: ListingRepository,
        private readonly clientRepository: ClientRepository,
    ) {}

    // tutti gli immobili per cui il cliente ha fatto un offerta
    // essendo una query presonalizzata è stata inserirta nel repository del listing
    async getListingByClientId(userId: string): Promise<Listing[]> {

      const uniqueListings = await this.listingRepository.getListingByClientId(userId);
      if (!uniqueListings) throw new NotFoundException('No listings found for this client');
       
        return uniqueListings;
    }

     

    async createOffer(
      createOfferDto: CreateOfferDto,
       listingId: string,
        user: UserItem
      ): Promise<PropertyOffer> {

        const {price} = createOfferDto;

        const listing = await this.listingRepository.findOne({where: {id: listingId}});
        if (!listing) throw new NotFoundException('Listing not found');

        if (listing.price < price) throw new BadRequestException('Price exceeds listing price');
      

        const offer = await this.offerRepository.create({
            price : price,
            date: new Date(),
            state: OfferState.PENDING,
            madeByUser: true,
            listing : listing,  
            client: { userId: user.id } as Client,
        });
        await this.offerRepository.save(offer);
        return offer;
    }

    async createOfferbyAgent(
      createOfferDto: CreateOfferDto,
      listingId: string,
      user: UserItem,
      clientId: string
    ): Promise<PropertyOffer> {

      const {price} = createOfferDto;

        const listing = await this.listingRepository.findOne({where: {id: listingId}});
        if (!listing) throw new NotFoundException('Listing not found');

        this.checkAuthorization(user,listing) //controllo permessi

        if (listing.price < price) throw new BadRequestException('Price exceeds listing price');
      
        const offer = await this.offerRepository.create({
            price : price,
            date: new Date(),
            state: OfferState.PENDING,
            madeByUser: false,
            listing :listing,  
            client: { userId: clientId } as Client,
        });
        await this.offerRepository.save(offer);
        return offer;
  }


   async updateOffer(
    offerId:string,
    updateOfferdto: UpdateOfferDto,
    user : UserItem,
): Promise<PropertyOffer> {

        const {status} = updateOfferdto;  
        
        // cerco l oggetto offerta tramite l id dell offerta
        const offer = await this.offerRepository.findOne({where: {id: offerId}});
        if (!offer) throw new NotFoundException('Offer not found');

        if(user.role === UserRoles.AGENT)
        { 
            const listing = await this.listingRepository.findOne({where: {id: offer.listing.id}});
            if (!listing) throw new UnauthorizedException('Listing not found');

            this.checkAuthorization(user, listing); //controllo permessi
        }

        const madeByUser = offer.madeByUser;

        //controllo i permessi di cambio di status dell offerta
        if(user.role === UserRoles.CLIENT)
        {
            //se l offerta è stata fatta da un cliente non può accetare o rifiutare la propria offerta
            if(madeByUser && status == (OfferState.ACCEPTED || OfferState.DECLINED)) 
            {
                throw new UnauthorizedException('Client cannot accept or decline an offer');
            }
        }
        else
        {
            //se l offerta è stata fatta da un agente non può accetare o rifiutare la propria del cliente
            if(!madeByUser && status == (OfferState.ACCEPTED || OfferState.DECLINED)) 
            {
                throw new UnauthorizedException(' cannot accept or decline an offer');
            }
        }
          //l' offerta in ambo i casi puo essere annullata

          //faccio l update
          // dopo aver preso l offerta come ogetto semplicemente cambiando la proprieta dell oggetto
          // e salvando l oggetto offerta
          offer.state = status;
          this.offerRepository.save(offer); 

          return offer;
    }

    async getOffersByAgentId(
        listingId: string,
        clientId: string,
        agent: UserItem,
      ): Promise<PropertyOffer[]> {

        const listing = await this.listingRepository.findOne({where: {id: listingId}});       
         if (!listing) throw new UnauthorizedException('Listing not found');

         this.checkAuthorization(agent, listing); //controllo permessi

        const offers = await this.offerRepository.find({
          where: {
            listing: { id: listingId },
            client: { userId: clientId } as Client,
          },
          relations: ['client', 'listing'],
            order: { date: 'ASC' },
        });

        if (!offers) {
            throw new Error('No offers found for this agent and client');
            }
      
        return offers;
      }


      async getClientsByListinigId(listingId: string, agent: UserItem): Promise<Client[]> {

        const listing = await this.listingRepository.findOne({where: {id: listingId}});
        if (!listing) throw new UnauthorizedException('Listing not found');

        this.checkAuthorization(agent, listing); //controllo permessi

        //essendo una query presonalizzata è stata inserirta nel repository del client
        //perchè non è una query standard di ricerca
        const uniqueClients = await this.clientRepository.findClientByListingId(listingId);
        if (!uniqueClients) throw new NotFoundException('No clients found for this listing');

          return uniqueClients;
    }

    getAllOffersByListingId(listingId: string, id: string): Promise<PropertyOffer[]> {
        const offers = this.offerRepository.find({
            where: {
                client: { userId: id } as Client,
                listing: { id: listingId },
            },
            relations: ['client', 'listing'],
            order: { date: 'ASC' },
        })
        if (!offers) {
            throw new Error('No offers found for this agent and client');
        }

        return offers;
    }

    async createExternalOffer(dto: CreateExternalOfferDto, user: UserItem , listingId :string): Promise<PropertyOffer> {

      const { price, guestEmail, guestName } = dto;
      if (!guestEmail && !guestName) throw new BadRequestException('Guest email or name is required');
    
      const listing = await this.listingRepository.findOne({ where: { id: listingId } });
      if (!listing) throw new NotFoundException('Listing not found');

      this.checkAuthorization(user,listing)
    
      const offer = this.offerRepository.create({
        price,
        date: new Date(),
        state: OfferState.PENDING,
        madeByUser: false,
        guestEmail,
        guestName,
        listing,
      });
    
      await this.offerRepository.save(offer);
      return offer;
    }
    


    checkAuthorization(user: UserItem, listing: Listing): void {
            if (user.agent && user.agent.userId != listing.agent.userId)
              throw new UnauthorizedException();
        
            if (user.supportAdmin && user.supportAdmin.agency !== listing.agency)
              throw new UnauthorizedException();
        
            if (user.manager && user.manager.agency !== listing.agency)
              throw new UnauthorizedException();
          }

}
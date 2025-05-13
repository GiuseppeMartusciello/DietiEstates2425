import {
  BadRequestException,
  Injectable,
  InternalServerErrorException,
  NotFoundException,
  UnauthorizedException,
} from '@nestjs/common';
import { OfferState } from 'src/common/types/offer-state';
import { Listing } from 'src/listing/Listing.entity';
import { Client } from 'src/client/client.entity';
import { PropertyOffer } from './property_offer.entity';
import { CreateOfferDto } from './dto/create-offer.dto';
import { UserItem } from 'src/common/types/userItem';
import { ListingRepository } from 'src/listing/listing.repository';
import { UpdateOfferDto } from './dto/update-offer.dto';
import { UserRoles } from 'src/common/types/user-roles';
import { CreateExternalOfferDto } from './dto/create-externalOffer.dto';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { NotificationService } from 'src/notification/notification.service';
import { NotificationType } from 'src/common/types/notification.enum';

@Injectable()
export class OfferService {
  constructor(
    @InjectRepository(PropertyOffer)
    private readonly offerRepository: Repository<PropertyOffer>,

    private readonly listingRepository: ListingRepository,

    @InjectRepository(Client)
    private readonly clientRepository: Repository<Client>,

    private readonly notificationService: NotificationService,
  ) {}

  // tutti gli immobili per cui il cliente ha fatto un offerta
  // essendo una query presonalizzata è stata inserirta nel repository del listing
  async getListingByClientId(userId: string): Promise<Listing[]> {

    const uniqueListings = await this.listingRepository
    .createQueryBuilder('listing')
    .innerJoinAndSelect('listing.propertyOffers', 'propertyOffer')
    .where('propertyOffer.client.userId = :userId', { userId })
    .distinct(true)
    .getMany();

    if (!uniqueListings)
      throw new NotFoundException('No listings found for this client');

    return uniqueListings;
  }

  //viene creata un offerta per un immobile da parte del cliente
  async createOffer(
    createOfferDto: CreateOfferDto,
    listingId: string,
    user: UserItem,
  ): Promise<PropertyOffer> {
    const { price } = createOfferDto;

    const listing = await this.listingRepository.findOne({
      where: { id: listingId },
    });
    if (!listing) throw new NotFoundException('Listing not found');

    if (listing.price < price)
      throw new BadRequestException('Price exceeds listing price');

    const offer = await this.offerRepository.create({
      price: price,
      date: new Date(),
      state: OfferState.PENDING,
      madeByUser: true,
      listing: listing,
      client: { userId: user.id } as Client,
    });

    await this.offerRepository.save(offer);

    //crea notifica specifica per una nuova offerta
     const notifica = await this.notificationService.createSpecificNotificationOffer(
      {
       title: 'New offer',
       description: 'New offer for your listing',
       category: NotificationType.SPECIFIC,
      },
      offer);

      if (!notifica)
      throw new InternalServerErrorException('Notification not created');

    


    return offer;
  }

 // viene creata un offerta per un immobile da parte dell agente
  async createOfferbyAgent(
    createOfferDto: CreateOfferDto,
    listingId: string,
    user: UserItem,
    clientId: string,
  ): Promise<PropertyOffer> {
    const { price } = createOfferDto;

    const listing = await this.listingRepository.findOne({
      where: { id: listingId },
    });
    if (!listing) throw new NotFoundException('Listing not found');

    this.checkAuthorization(user, listing); //controllo permessi

    if (listing.price < price)
      throw new BadRequestException('Price exceeds listing price');

    const offer = await this.offerRepository.create({
      price: price,
      date: new Date(),
      state: OfferState.PENDING,
      madeByUser: false,
      listing: listing,
      client: { userId: clientId } as Client,
    });
    await this.offerRepository.save(offer);

    //crea notifica specifica per una nuova offerta
    const notifica = await this.notificationService.createSpecificNotificationOffer(
      {
       title: 'New offer',
       description: 'New offer for your listing',
       category: NotificationType.SPECIFIC,
      },
      offer);

     if (!notifica)
      throw new InternalServerErrorException('Notification not created');

    return offer;
  }


//offerta viene aggiornata se viene rifiutata o accettata o annullata
  async updateOffer(
    offerId: string,
    updateOfferdto: UpdateOfferDto,
    user: UserItem,
  ): Promise<PropertyOffer> {
    const { status } = updateOfferdto;

    // cerco l oggetto offerta tramite l id dell offerta
    const offer = await this.offerRepository.findOne({
      where: { id: offerId },
    });
    if (!offer) throw new NotFoundException('Offer not found');

    if (user.role === UserRoles.AGENT) {
      const listing = await this.listingRepository.findOne({
        where: { id: offer.listing.id },
      });
      if (!listing) throw new UnauthorizedException('Listing not found');

      this.checkAuthorization(user, listing); //controllo permessi
    }

    const madeByUser = offer.madeByUser;

    //controllo i permessi di cambio di status dell offerta
    if (user.role === UserRoles.CLIENT) {
      //se l offerta è stata fatta da un cliente non può accetare o rifiutare la propria offerta
      if (
        madeByUser &&
        status == (OfferState.ACCEPTED || OfferState.DECLINED)
      ) {
        throw new UnauthorizedException(
          'Client cannot accept or decline an offer',
        );
      }
    } else {
      //se l offerta è stata fatta da un agente non può accetare o rifiutare la propria del cliente
      if (
        !madeByUser &&
        status == (OfferState.ACCEPTED || OfferState.DECLINED)
      ) {
        throw new UnauthorizedException(' cannot accept or decline an offer');
      }
    }
    //l' offerta in ambo i casi puo essere annullata

    //faccio l update
    // dopo aver preso l offerta come ogetto semplicemente cambiando la proprieta dell oggetto
    // e salvando l oggetto offerta
    offer.state = status;
    this.offerRepository.save(offer);


    //crea notifica specifica 
    //attenzione ! la notifica viene create l update è sull offerta ma la notifica è nuova
    const notifica = await this.notificationService.createSpecificNotificationOffer(
      {
       title: 'Your offer has been updated',
       description: 'check out the new status of your offer',
       category: NotificationType.SPECIFIC,
      },
      offer);

    if (!notifica)
      throw new InternalServerErrorException('Notification not created');

    return offer;
  }


  // restituisce tutte le offerte per un agente
  async getOffersByAgentId(
    listingId: string,
    clientId: string,
    agent: UserItem,
  ): Promise<PropertyOffer[]> {
    const listing = await this.listingRepository.findOne({
      where: { id: listingId },
    });
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

  async getClientsByListinigId(
    listingId: string,
    agent: UserItem,
  ): Promise<Client[]> {
    const listing = await this.listingRepository.findOne({
      where: { id: listingId },
    });
    if (!listing) throw new UnauthorizedException('Listing not found');

    this.checkAuthorization(agent, listing); //controllo permessi

    //essendo una query presonalizzata è stata inserirta nel repository del client
    //perchè non è una query standard di ricerca
    const uniqueClients = await this.findClientByListingId(listingId);
    if (!uniqueClients)
      throw new NotFoundException('No clients found for this listing');

    return uniqueClients;
  }

  getAllOffersByListingId(
    listingId: string,
    id: string,
  ): Promise<PropertyOffer[]> {
    const offers = this.offerRepository.find({
      where: {
        client: { userId: id } as Client,
        listing: { id: listingId },
      },
      relations: ['client', 'listing'],
      order: { date: 'ASC' },
    });
    if (!offers) {
      throw new Error('No offers found for this agent and client');
    }

    return offers;
  }

  async createExternalOffer(
    dto: CreateExternalOfferDto,
    user: UserItem,
    listingId: string,
  ): Promise<PropertyOffer> {
    const { price, guestEmail, guestName } = dto;
    if (!guestEmail && !guestName)
      throw new BadRequestException('Guest email or name is required');

    const listing = await this.listingRepository.findOne({
      where: { id: listingId },
    });
    if (!listing) throw new NotFoundException('Listing not found');

    this.checkAuthorization(user, listing);

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

    //la notifica in questo caso non viene creata poiche è stesso l agente che la crea, la notifica dovrebbe
    //notificare lo stesso agente e non avrebbe senso

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

  // PRIVATE HELPERS
  private async findClientByListingId(listingId: string): Promise<Client[]> {
    const clients = await this.clientRepository
      .createQueryBuilder('client')
      .innerJoinAndSelect('client.propertyOffers', 'propertyOffer')
      .innerJoin('propertyOffer.listing', 'listing')
      .where('listing.id = :listingId', { listingId })
      .distinct(true)
      .getMany();

    return clients;
  }

}

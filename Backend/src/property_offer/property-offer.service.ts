import {
  BadRequestException,
  ConflictException,
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
import { CreateExternalOfferDto } from './dto/create-externalOffer.dto';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { NotificationService } from 'src/notification/notification.service';
import { NotificationType } from 'src/common/types/notification.enum';
import { ListingResponse } from 'src/listing/dto/listing-with-image.dto';
import { ListingService } from 'src/listing/listing.service';
import { instanceToPlain } from 'class-transformer';
import { ClientWithLastOfferDto } from './dto/last-offer.dto';

@Injectable()
export class OfferService {
  constructor(
    @InjectRepository(PropertyOffer)
    private readonly offerRepository: Repository<PropertyOffer>,

    private readonly listingRepository: ListingRepository,

    private readonly notificationService: NotificationService,

    private readonly listingService: ListingService,
  ) {}

  // tutti gli immobili per cui il cliente ha fatto un offerta
  // essendo una query presonalizzata è stata inserirta nel repository del listing
  async getListingByClientId(userId: string): Promise<ListingResponse[]> {
    const listings = await this.listingRepository
      .createQueryBuilder('listing')
      .innerJoinAndSelect('listing.propertyOffers', 'propertyOffer')
      .where('propertyOffer.client.userId = :userId', { userId })
      .distinct(true)
      .getMany();

    const response: ListingResponse[] = await Promise.all(
      listings.map(async (listing) => ({
        ...(instanceToPlain(listing) as Listing),
        imageUrls: [
          ...(await this.listingService.getImagesForListing(listing.id)).slice(
            0,
            1,
          ),
        ],
      })),
    );

    return response;
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

    //await this.checkValidate(listing.id);

    this.checkPrice(listing.price, price);

    return this.createOfferEntity(price, listing, user.id, true);
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

    //await this.checkValidate(listing.id);

    this.checkPrice(listing.price, price);

    const offer = await this.createOfferEntity(price, listing, clientId, false);

    //crea notifica specifica per una nuova offerta

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
      relations: ['listing', 'client'],
    });
    if (!offer) throw new NotFoundException('Offer not found');

    if (status === OfferState.ACCEPTED) {
      await this.checkValidate(offer.listing.id);
    }

    //controllo se l offerta esiste e se è in stato PENDING
    else if (offer.state !== OfferState.PENDING)
      throw new BadRequestException('Offer already processed');

    //se non è un cliente
    //si recupera l immobile tramite l offerta e poi si controllano se i permessi sono validi
    if (!user.client) {
      const listing = await this.listingRepository.findOne({
        where: { id: offer.listing.id },
      });
      if (!listing) throw new UnauthorizedException('Listing not found');

      this.checkAuthorization(user, listing); //controllo permessi
    }

    const madeByUser = offer.madeByUser;

    if (user.client) {
      //se l offerta è stata fatta da un cliente non può accetare o rifiutare la propria offerta
      if (
        madeByUser &&
        status == (OfferState.ACCEPTED || OfferState.DECLINED)
      ) {
        throw new UnauthorizedException(
          'Client cannot accept or decline an offer',
        );
      }
    } else if (
      !madeByUser &&
      status == (OfferState.ACCEPTED || OfferState.DECLINED)
    ) {
      //se l offerta è stata fatta da un agente non può accetare o rifiutare la propria del cliente
      throw new UnauthorizedException(' cannot accept or decline an offer');
    }
    //l' offerta in ambo i casi puo essere annullata

    //update
    offer.state = status;
    await this.offerRepository.save(offer);

    //crea notifica specifica
    //attenzione ! la notifica viene create l update è sull offerta ma la notifica è nuova
    const notifica =
      await this.notificationService.createSpecificNotificationOffer(
        {
          title: 'Un offerta è stata cambiata !',
          description:
            'Controlla l offerta per :' +
            offer.listing.title +
            'il suo nuovo stato adesso è' +
            status,
          category: NotificationType.SPECIFIC,
        },
        offer,
        true,
      );

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

    return offers;
  }

  async getExternalOffers(
    listingId: string,
    user: UserItem,
  ): Promise<ClientWithLastOfferDto[]> {
    const listing = await this.listingRepository.findOne({
      where: { id: listingId },
      relations: ['agent'],
    });
    if (!listing) throw new BadRequestException('Listing not found');

    this.checkAuthorization(user, listing);

    const externalOffers = await this.offerRepository
      .createQueryBuilder('offer')
      .where('offer.listingId = :listingId', { listingId })
      .andWhere('offer.guestName IS NOT NULL')
      .andWhere('offer.guestSurname IS NOT NULL')
      .andWhere('offer.guestEmail IS NOT NULL')
      .orderBy('offer.date', 'ASC')
      .getMany();

    const result: ClientWithLastOfferDto[] = externalOffers.map((offer) => {
      // Offerta da utente esterno (ospite)
      return {
        userId: null,
        name: offer.guestName ?? '',
        surname: offer.guestSurname ?? '',
        email: offer.guestEmail ?? '',
        phone: null,
        lastOffer: {
          id: offer.id,
          price: offer.price,
          date: offer.date,
          state: offer.state,
          madeByUser: offer.madeByUser,
        },
      };
    });

    return result;
  }

  async getLatestOffersByListingId(
    listingId: string,
    user: UserItem,
  ): Promise<ClientWithLastOfferDto[]> {
    const listing = await this.listingRepository.findOne({
      where: { id: listingId },
    });
    if (!listing) throw new UnauthorizedException('Listing not found');
    this.checkAuthorization(user, listing); // controllo permessi
    const offers = await this.offerRepository
      .createQueryBuilder('offer')
      .distinctOn(['offer.clientUserId']) // clientUserId è la FK nel DB
      .innerJoinAndSelect('offer.client', 'client')
      .leftJoinAndSelect('client.user', 'user')
      .innerJoinAndSelect('offer.listing', 'listing')
      .where('offer.listingId = :listingId', { listingId })
      .andWhere('offer.madeByUser = true')
      .orderBy('offer.clientUserId', 'ASC')
      .addOrderBy('offer.date', 'DESC') // prende l'ultima offerta per client
      .getMany();

    const result: ClientWithLastOfferDto[] = offers.map((offer) => {
      // Offerta da utente registrato
      return {
        userId: offer.client?.userId,
        name: offer.client?.user?.name ?? '',
        surname: offer.client?.user?.surname ?? '',
        email: offer.client?.user?.email ?? '',
        phone: offer.client?.user?.phone ?? null,
        lastOffer: {
          id: offer.id,
          price: offer.price,
          date: offer.date,
          state: offer.state,
          madeByUser: offer.madeByUser,
        },
      };
    });

    return result;
  }

  async getClientsByListingId(listingId: string): Promise<PropertyOffer[]> {
    //essendo una query presonalizzata è stata inserirta nel repository del client
    //perchè non è una query standard di ricerca
    const uniqueClients = await this.findClientByListingId(listingId);

    return uniqueClients;
  }

  async getOffersByListingAndClient(
    listingId: string,
    clientId: string,
  ): Promise<PropertyOffer[]> {
    return this.offerRepository.find({
      where: {
        listing: { id: listingId },
        client: { userId: clientId } as Client,
      },
      relations: ['client', 'listing'],
      order: { date: 'ASC' },
    });
  }

  async createExternalOffer(
    dto: CreateExternalOfferDto,
    user: UserItem,
    listingId: string,
  ): Promise<PropertyOffer> {
    const { price, guestEmail, guestName, guestSurname } = dto;
    if (!guestEmail && !guestName && !guestSurname)
      throw new BadRequestException('Email, name and surname are required');

    const listing = await this.listingRepository.findOne({
      where: { id: listingId },
    });
    if (!listing) throw new NotFoundException('Listing not found');

    this.checkAuthorization(user, listing);

    this.checkPrice(listing.price, price);

    const offer = this.offerRepository.create({
      price,
      date: new Date(),
      state: OfferState.PENDING,
      madeByUser: true,
      guestEmail,
      guestName,
      guestSurname,
      listing,
    });

    await this.offerRepository.save(offer);

    //la notifica in questo caso non viene creata poiche è stesso l agente che la crea, la notifica dovrebbe
    //notificare lo stesso agente e non avrebbe senso

    return offer;
  }

  checkAuthorization(user: UserItem, listing: Listing): void {
    console.log('Controllo permessi di user: ', user);
    if (user.agent && user.agent.userId != listing.agent.userId)
      throw new UnauthorizedException();

    if (user.supportAdmin && user.supportAdmin.agency.id != listing.agency.id)
      throw new UnauthorizedException();

    if (user.manager && user.manager.agency.id != listing.agency.id)
      throw new UnauthorizedException();
  }

  private async findClientByListingId(
    listingId: string,
  ): Promise<PropertyOffer[]> {
    const offers = await this.offerRepository.find({
      where: {
        listing: { id: listingId },
      },
      relations: ['client', 'listing'],
      order: { date: 'ASC' },
    });

    return offers;
  }

  private async checkValidate(listingId: string) {
    const exist = await this.offerRepository.findOne({
      where: { state: OfferState.ACCEPTED, listing: { id: listingId } },
    });

    if (exist) throw new ConflictException('An offer accepted already exist');
  }

  private checkPrice(listingPrice: number, userOffer: number) {
    if (listingPrice < userOffer)
      throw new BadRequestException(
        "L'offerta non puo' essere superiore al prezzo dell'immobile. (€410000)",
      );

    if (userOffer <= 0)
      throw new BadRequestException('Price can t be < then 0');
  }

  private async createOfferEntity(
    price: number,
    listing: Listing,
    clientId: string,
    madeByUser: boolean,
  ) {
    const offer = this.offerRepository.create({
      price: price,
      date: new Date(),
      state: OfferState.PENDING,
      madeByUser: madeByUser,
      listing: listing,
      client: { userId: clientId } as Client,
    });

    await this.offerRepository.save(offer);
    //crea notifica specifica per una nuova offerta
    const notifica =
      await this.notificationService.createSpecificNotificationOffer(
        {
          title: 'Nuova offerta per ' + listing.title,
          description: `Ti è stata proposta una offerta di ${price}€ per l immobile: ${listing.title}.`,
          category: NotificationType.SPECIFIC,
        },
        offer,
        false,
      );

    if (!notifica)
      throw new InternalServerErrorException('Notification not created');

    return offer;
  }
}

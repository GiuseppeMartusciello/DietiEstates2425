import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Notification } from './notification.entity';
import { UserNotification } from './user-notification.entity';
import { UserItem } from 'src/common/types/userItem';
import { CreateNotificationDto } from './dto/create-notification.dto';
import { Listing } from 'src/listing/Listing.entity';
import { Client } from 'src/client/client.entity';
import { PropertyOffer } from 'src/property_offer/property_offer.entity';
import { User } from 'src/auth/user.entity';


@Injectable()
export class NotificationService {
  constructor(
    @InjectRepository(Notification)
    private readonly notificationRepository: Repository<Notification>,

    @InjectRepository(UserNotification)
    private readonly userNotificationRepository: Repository<UserNotification>,

    @InjectRepository(Client)
    private readonly clientRepository: Repository<Client>,
  ) {}

  //viene creata una notifica promozionale per un immobile

  async createPromotionalNotification(
    user: UserItem,
    createNotificationDto: CreateNotificationDto,
    listingId: string,
  ): Promise<Notification> {
    const result = this.notificationRepository.create({
      ...createNotificationDto,
      date: new Date(),
      listing: { id: listingId } as Listing,
      createdBy: { id: user.id } as User, // Associa l'utente che crea la notifica
    });

    const listing = await this.notificationRepository.manager.findOne(Listing, {
      where: { id: listingId },
    });

    if (!listing) {
      throw new Error('Listing not found');
    }

    //viene salvata la notifica creata
    const savedNotification = await this.notificationRepository.save(result);

    //recupero tutti i clienti che hanno fatto la ricerca in quella municipalità
    const municipality = listing.municipality;

    const AllClient = await this.clientRepository
      .createQueryBuilder('client')
      .innerJoin('client.research', 'research')
      .where('client.searchNotification = true')
      .andWhere('research.municipality = :municipality', { municipality })
      .getMany();

    //testare! di nuovo
    //vengono messi tutti i client in una map

    const userNotifications = AllClient.map((user) => ({
      user: { id: user.userId }, // entità parziale User
      notification: { id: savedNotification.id }, // entità parziale Notification
      isRead: false,
    }));

    //vengono create le entita userNotification
    await this.userNotificationRepository.save(userNotifications);

    return savedNotification;
  }

  //viene creata una notifica specifica per una offerta
  async createSpecificNotificationOffer(
    createNotificationDto: CreateNotificationDto,
    propertyOffer: PropertyOffer,
    update : boolean,
  ): Promise<Notification> {

    //se l offerta è stata fatta da un cliente viene notificato l agente
    //l agente vien recuperato da propertyOffer e listing
    //se l offerta è stata fatta da un agente viene notificato il cliente
    //il cliente viene recuperato da propertyOffer

    //in caso di update la logica è esattamente il contrario

  let user: { userId: string };

  if (!update) {
    // Caso normale: offerta nuova
    user = propertyOffer.madeByUser
      ? propertyOffer.listing.agent
      : propertyOffer.client;
  } else {
    // Caso update: inverti logica
    user = propertyOffer.madeByUser
      ? propertyOffer.client
      : propertyOffer.listing.agent;
  }
    
    const result = this.notificationRepository.create({
      ...createNotificationDto,
      date: new Date(),
      propertyOffer: propertyOffer,
      createdBy: { id: user.userId }, // Associa l'utente che crea la notifica
    });

    const savedNotification = await this.notificationRepository.save(result);
    //viene creata l entita userNotification
    const userNotification = this.userNotificationRepository.create({
      user: { id: user.userId },
      notification: { id: savedNotification.id },
      isRead: false,
    });

    //console.log('Saved Notification:', result);
    //console.log('UserNotification:', userNotification);

    //viene salvata la notifica creata
    await this.userNotificationRepository.save(userNotification);

    return savedNotification;
  }


  //restituisce tutte le notifiche non lette per un utente
  //viene utilizzato una qery builder personalizzata

 async Notifications(userId: string): Promise<Notification[]> {
  const notifications = await this.notificationRepository
    .createQueryBuilder('notification')
    .innerJoinAndSelect('notification.userNotifications', 'userNotification', 'userNotification.user.id = :userId', { userId })
    .orderBy('notification.date', 'DESC')
    .getMany();

  return notifications;
}


  async NotificationById(notificationId: string): Promise<Notification> {
    const notification = await this.notificationRepository.findOneOrFail({
      where: { id: notificationId },
    });
    if (!notification) throw new Error('Notification not found');

    return notification;
  }

  //viene fatta update su isRead quando una notifica viene letta
  async Notification(userNotificationId: string): Promise<void> {
    await this.userNotificationRepository.update(
      { id: userNotificationId },
      { isRead: true },
    );
  }
}

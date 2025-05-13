import {  Injectable, NotFoundException } from "@nestjs/common";

import { InjectRepository } from "@nestjs/typeorm";
import { Repository } from "typeorm";
import { Notification } from "./notification.entity";
import { UserNotification } from "./user-notification.entity";
import { UserItem } from "src/common/types/userItem";
import { CreateNotificationDto } from "./dto/create-notification.dto";
import { Listing } from "src/listing/Listing.entity";
import { Client } from "src/client/client.entity";
import { PropertyOffer } from "src/property_offer/property_offer.entity";

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
     listingId: string
    ): Promise<Notification> {

    const { title, description, category } = createNotificationDto;

    const result = this.notificationRepository.create({
      title: title,
      description: description,
      date: new Date(),
      category: category,
      listing: {id : listingId} as Listing, 
    });

  //viene salvata la notifica creata
  const savedNotification = await this.notificationRepository.save(result)

//vengono cercati tutti i clienti che hanno attivato la notifica
    const AllUsers = await this.clientRepository.find({
      where: { promotionalNotification: true },
    });

//vengono messi tutti i client in una map 
  const userNotifications = AllUsers.map((user) => ({
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
    propertyOffer: PropertyOffer 
  ): Promise<Notification> {

    const { title, description, category } = createNotificationDto;

    const result = this.notificationRepository.create({
      title: title,
      description: description,
      date: new Date(),
      category: category,
      propertyOffer: propertyOffer, 
    });

    const savedNotification = await this.notificationRepository.save(result);

    //se l offerta è stata fatta da un cliente viene notificato l agente
    //l agente vien recuperato da propertyOffer e listing
    //se l offerta è stata fatta da un agente viene notificato il cliente
    //il cliente viene recuperato da propertyOffer 
    const user = propertyOffer.madeByUser
    ? propertyOffer.listing.agent
    : propertyOffer.client ;
  

    const userNotification = this.userNotificationRepository.create({
      user: {id: user.userId},
      notification: {id :savedNotification.id},
      isRead: false,
    });

    //viene salvata la notifica creata
   
    
    //viene creata l entita userNotification
    await this.userNotificationRepository.save(userNotification);

    return savedNotification;
  } 

  
  //restituisce tutte le notifiche non lette per un utente
  //viene utilizzato una qery builder personalizzata
async getNotification(userId: string): Promise<Notification[]> {
  const notifications = await this.notificationRepository
    .createQueryBuilder('notification')
    .innerJoin('notification.userNotifications', 'userNotification')
    .innerJoin('userNotification.user', 'user')
    .where('user.id = :userId', { userId })
    .andWhere('userNotification.isRead = false')
    .orderBy('notification.date', 'DESC')
    .getMany();

  if (!notifications || notifications.length === 0) {
    throw new NotFoundException('Nessuna notifica trovata');
  }

  return notifications;
}

  async getNotificationById( notificationId: any): Promise<Notification> {

    const notification = await this.notificationRepository.findOneOrFail({
      where: { id: notificationId },
    });
    if (!notification) throw new Error('Notification not found');

    return notification;
  }


//viene fatta update su isRead quando una notifica viene letta
  async updateNotification(
    user: UserItem,
    userNotificationId: string
  ): Promise<void> 
  {
    await this.userNotificationRepository.update({id: userNotificationId}, {isRead: true});
  }








  //viene creata una notifica specifica per un immobile
  // TODO: da capire bene questa cosa; ovvero vogliamo che le notifiche si generino autamticamente alla creazione di un immobile?
  // se si serve questo metodo
  // se no basta il metodo createPromotionalNotification nella quale un admin crea una notifica per un immobile
  // la principale differeenza è che nel secondo caso la notfica viene inviata a tutti i clienti che hanno attivato la notifica attivata
  // nel primo si potebbe andare a cercare gli utenti che hanno cercato un immobile in una determianta zona
  /*  async createSpecificNotificationListing(createNotificationDto: CreateNotificationDto,listing: Listing ): Promise<Notification> {

    const { title, description, category } = createNotificationDto;

    const result = this.notificationRepository.create({
      title: title,
      description: description,
      date: new Date(),
      category: category,
      listing: listing, 
    });

    //se l offerta è stata fatta da un cliente viene notificato l agente
    //l agente vien recuperato da propertyOffer e listing
    //se l offerta è stata fatta da un agente viene notificato il cliente
    //il cliente viene recuperato da propertyOffer 


  

    const userNotification = await this.userNotificationRepository.create({
      user: {id: user.userId} as User,
      notification: result,
      isRead: false,
    });

    //viene salvata la notifica creata
    const check =await this.notificationRepository.save(result)
    
    //viene creata l entita userNotification
    await this.userNotificationRepository.save(userNotification);

    return check;
  } 
  */
}
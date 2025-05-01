import {
  Entity,
  PrimaryGeneratedColumn,
  Column,
  ManyToMany,
  JoinTable,
  ManyToOne,
  OneToMany,
} from 'typeorm';


import { Listing } from 'src/listing/Listing.entity';
import { NotificationType } from 'src/common/types/notification.enum';
import { PropertyOffer } from 'src/property_offer/property_offer.entity';
import { UserNotification } from './user-notification.entity';

@Entity()
export class Notification {

  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column({type: 'enum', enum: NotificationType})
  category: string;

  @Column()
  title: string;

  @Column('text')
  description: string;

  @Column({type: 'date'})
  date: Date;

  @ManyToOne(() => Listing, (listing) => listing.notifications,
  {
    onDelete: 'CASCADE',
    nullable: true,
  })
  listing: Listing;

  @ManyToOne(() => PropertyOffer, (propertyoffer)=> propertyoffer.notifications,
  {
    onDelete: 'CASCADE',
    nullable: true, 
  })
  propertyOffer: PropertyOffer;

  @OneToMany(()=> UserNotification, (userNotification) => userNotification.notification)
  userNotifications: UserNotification[];

  }

  // si erano riscontrati dubbi riguardo alle relazioni che la classe notifica avrebbe dovuto avere
  // con le classi di user, agent, admin, manager,
  // in quando possibili creatori di notifiche
  // si è deciso di non implementare queste relazioni in quanto non necessarie
  // queste informazioni quando necesare possono essere recuperate da propertyOffer e listing





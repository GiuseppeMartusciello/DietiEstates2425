import {
  Entity,
  PrimaryGeneratedColumn,
  Column,
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

  @Column({ type: 'enum', enum: NotificationType })
  category: string;

  @Column()
  title: string;

  @Column('text')
  description: string;

  @Column({ type: 'date' })
  date: Date;

  @ManyToOne(() => Listing, (listing) => listing.notifications, {
    onDelete: 'CASCADE',
    nullable: true,
  })
  listing: Listing;

  @ManyToOne(
    () => PropertyOffer,
    (propertyoffer) => propertyoffer.notifications,
    {
      onDelete: 'CASCADE',
      nullable: true,
    },
  )
  propertyOffer: PropertyOffer;

  @OneToMany(
    () => UserNotification,
    (userNotification) => userNotification.notification,
  )
  userNotifications: UserNotification[];
  //AGGIUNGERE UN RELAZIONE CON L'UTENTE CHE HA CREATO LA NOTIFICA
}

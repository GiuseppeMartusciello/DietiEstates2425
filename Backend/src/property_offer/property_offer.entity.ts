import { Client } from 'src/client/client.entity';
import { OfferState } from 'src/common/types/offer-state';
import { Listing } from 'src/listing/Listing.entity';
import { Notification } from '../notification/notification.entity';

import {
  Column,
  Entity,
  ManyToOne,
  OneToMany,
  PrimaryGeneratedColumn,
} from 'typeorm';

@Entity()
export class PropertyOffer {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column()
  price: number;

  @Column({ type: 'timestamp' })
  date: Date;

  @Column({ type: 'enum', enum: OfferState, default: OfferState.PENDING })
  state: OfferState;

  @Column({ type: 'boolean', default: false })
  madeByUser: boolean;

  //email e nome servono per i guest, giusto per avere delle informazioni che lo riguardano
  @Column({ nullable: true })
  guestEmail?: string;

  @Column({ nullable: true })
  guestName?: string;

  //si è dovuto mettere nullable true per poter far accettare dal sistema le richieste esterne
  //ovvero quando un admin aggiunge un offerta da un cliente esterno al sistema
  @ManyToOne(() => Client, (client) => client.propertyOffers, {
    onDelete: 'CASCADE',
    nullable: true,
  })
  client: Client;

  @ManyToOne(() => Listing, (listing) => listing.propertyOffers, {
    onDelete: 'CASCADE',
  })
  listing: Listing;

  @OneToMany(() => Notification, (notification) => notification.propertyOffer)
  notifications: Notification[]; //aggiunto per le notifiche
}

import {
  Column,
  Entity,
  JoinColumn,
  ManyToMany,
  OneToMany,
  OneToOne,
  PrimaryColumn,
} from 'typeorm';
import { User } from '../auth/user.entity';
import { PropertyOffer } from '../property_offer/property_offer.entity';
import { Research } from '../research/research.entity';
import { Notification } from '../notification/notification.entity';

@Entity()
export class Client {
  @PrimaryColumn('uuid')
  userId: string;

  @Column({ type: 'boolean' })
  promotionalNotification: boolean;

  @Column({ type: 'boolean' })
  offerNotification: boolean;

  @Column({ type: 'boolean' })
  searchNotification: boolean;

  @OneToOne(() => User, {
    nullable: true,
    onDelete: 'CASCADE',
  })
  @JoinColumn({ name: 'userId' })
  user: User;

  @OneToMany(() => PropertyOffer, (property) => property.client)
  propertyOffers: PropertyOffer[];

  @OneToMany(() => Research, (research) => research.client)
  research: Research[];

  @ManyToMany(() => Notification, (notification) => notification.clients)
  notifications: Notification[];
}

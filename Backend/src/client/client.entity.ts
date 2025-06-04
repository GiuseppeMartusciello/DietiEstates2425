import {
  Column,
  Entity,
  JoinColumn,
  OneToMany,
  OneToOne,
  PrimaryColumn,
} from 'typeorm';
import { User } from '../auth/user.entity';
import { PropertyOffer } from '../property_offer/property_offer.entity';
import { Research } from '../research/research.entity';
import { Exclude } from 'class-transformer';

@Entity()
export class Client {
  @PrimaryColumn('uuid')
  userId: string;

  @Column({ nullable: true })
  address: string;

  @Column({ type: 'boolean', default: true })
  promotionalNotification: boolean;

  @Column({ type: 'boolean', default: true })
  offerNotification: boolean;

  @Column({ type: 'boolean', default: true })
  searchNotification: boolean;

  @OneToOne(() => User, {
    nullable: true,
    onDelete: 'CASCADE',
  })
  @JoinColumn({ name: 'userId' })
  @Exclude()
  user: User;

  @OneToMany(() => PropertyOffer, (property) => property.client)
  propertyOffers: PropertyOffer[];

  @OneToMany(() => Research, (research) => research.client)
  research: Research[];
}

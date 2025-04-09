import { Client } from 'src/client.entity';
import { OfferState } from 'src/common/types/offer-state';
import {
  Column,
  Entity,
  ManyToMany,
  ManyToOne,
  PrimaryColumn,
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

  @ManyToOne(() => Client, (client) => client.propertyOffers, {
    onDelete: 'CASCADE',
  })
  client: Client;
}

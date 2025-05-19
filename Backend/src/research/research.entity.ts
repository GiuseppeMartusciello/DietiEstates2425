import {
  Column,
  Entity,
  ManyToOne,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { Client } from '../client/client.entity';
import { Exclude } from 'class-transformer';
import { SearchType } from 'src/common/types/searchType.enum';
import { ListingCategory } from 'src/common/types/listing-category';


@Entity()
export class Research {

  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column({type: 'enum', enum: SearchType})
  searchType: SearchType;

  @Column({type: 'date'})
  date: Date;

  @Column({nullable: true})
  municipality: string;

  @Column({nullable: true})
  latitude: number;

  @Column({nullable: true})
  longitude: number;

  @Column({nullable: true})
  radius: number;

  @Column({nullable: true})
  minPrice: number;

  @Column({nullable: true})
  maxPrice: number;

  @Column({nullable: true})
  numberOfRooms: number;

  @Column({nullable: true, type: 'enum', enum: ListingCategory})
  category: ListingCategory;

  @Column({nullable: true})
  minSize: string; //dimensione in metri quadri

  @Column({nullable: true})
  energyClass: string;

  @Column({nullable: true})
  hasElevator: boolean;

  @Column({nullable: true})
  hasAirConditioning: boolean;

  @Column({nullable: true})
  hasGarage: boolean;

  @ManyToOne(() => Client, (client) => client.research, { onDelete: 'CASCADE' })
  @Exclude()
  client: Client;
}

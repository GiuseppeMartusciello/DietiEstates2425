import { Entity, PrimaryGeneratedColumn, Column, ManyToOne } from 'typeorm';
import { ListingCategory } from '../common/types/listing-category';
import { Agency } from '../agency/agency.entity';

@Entity()
export class Listing {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column()
  address: string;

  @Column()
  comune: string;
  @Column()
  city: string;

  @Column()
  postalCode: string;

  @Column()
  province: string;

  @Column()
  size: string; //dimensione in metri quadri

  @Column()
  position: string; // coordinate

  @Column('int')
  numberOfRooms: number;

  @Column()
  energyClass: string;

  @Column('text', { array: true })
  nearbyPlaces: string[]; //posti vicini

  @Column('text')
  description: string;

  @Column('float')
  price: number;

  @Column({ type: 'enum', enum: ListingCategory })
  category: ListingCategory; // categoria di contratto (vendita, affitto ecc)

  @Column()
  floor: string; //piano dell'appartamento

  @Column()
  hasElevator: boolean;

  @Column()
  hasAirConditioning: boolean;

  @Column()
  hasGarage: boolean;

  @ManyToOne(() => Agency, (agency) => agency.listings, {
    onDelete: 'CASCADE',
    nullable: true,
  })
  agency: Agency;
}

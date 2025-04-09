import {
  Column,
  Entity,
  ManyToOne,
  PrimaryColumn,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { Client } from '../client/client.entity';

@Entity()
export class Research {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column()
  date: Date;

  @Column()
  text: string;

  @ManyToOne(() => Client, (client) => client.research)
  client: Client;
}

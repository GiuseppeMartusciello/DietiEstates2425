import {
  Entity,
  PrimaryGeneratedColumn,
  Column,
  ManyToMany,
  JoinTable,
} from 'typeorm';
import { Client } from './client.entity';

@Entity()
export class Notification {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column()
  category: string;

  @Column()
  title: string;

  @Column()
  description: string;

  @Column()
  date: Date;

  // Associazione molti-a-molti con Client
  @ManyToMany(() => Client, (client) => client.notifications)
  @JoinTable()
  clients: Client[];
}

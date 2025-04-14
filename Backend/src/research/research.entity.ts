import {
  Column,
  Entity,
  ManyToOne,
  PrimaryColumn,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { Client } from '../client/client.entity';
import { Exclude } from 'class-transformer';

@Entity()
export class Research {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column()
  date: Date;

  @Column()
  text: string;

  @ManyToOne(() => Client, (client) => client.research, { onDelete: 'CASCADE' })
  @Exclude()
  client: Client;
}

import {
  Entity,
  JoinColumn,
  OneToOne,
  PrimaryColumn,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { User } from '../auth/user.entity';
import { Agency } from '../agency/agency.entity';

@Entity()
export class Manager {
  @PrimaryColumn('uuid')
  userId: string;

  @OneToOne(() => User, (user) => user.id, {
    onDelete: 'CASCADE',
  })
  @JoinColumn({ name: 'userId' })
  user: User;

  @OneToOne(() => Agency, (agency) => agency.id, { onDelete: 'CASCADE' })
  agency: Agency;
}

import {
  Column,
  Entity,
  OneToMany,
  OneToOne,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { Agent } from './agent.entity';
import { Listing } from 'src/Listing.entity';
import { Manager } from './agency-manager.entity';
import { SupportAdmin } from './support-admin.entity';

@Entity()
export class Agency {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column()
  name: string;

  @Column()
  legalAddress: string;

  @Column()
  phone: string;

  @Column()
  vatNumber: string;

  @OneToMany(() => Agent, (agent) => agent.agency)
  agents: Agent[];

  @OneToOne(() => Manager, (manager) => manager.agency)
  manager: Manager;

  @OneToMany(() => SupportAdmin, (supportAdmin) => supportAdmin.agency)
  supportAdmins: SupportAdmin[];

  @OneToMany(() => Listing, (listing) => listing.agency)
  listings: Listing[];
}

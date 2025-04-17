import {
  Column,
  Entity,
  OneToMany,
  OneToOne,
  PrimaryGeneratedColumn,
  Unique,
} from 'typeorm';
import { Agent } from '../agent/agent.entity';
import { Listing } from 'src/listing/Listing.entity';
import { Manager } from '../agency-manager/agency-manager.entity';
import { SupportAdmin } from '../support-admin/support-admin.entity';

@Entity()
export class Agency {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column({ unique: true })
  name: string;

  @Column()
  legalAddress: string;

  @Column({ unique: true })
  phone: string;

  @Column({ unique: true })
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

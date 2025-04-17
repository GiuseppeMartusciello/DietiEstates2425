import { Exclude } from 'class-transformer';
import { Provider } from 'src/common/types/provider.enum';
import { UserRoles } from 'src/common/types/user-roles';
import { Column, Entity, PrimaryGeneratedColumn, Unique } from 'typeorm';

@Entity()
export class User {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column()
  name: string;

  @Column()
  surname: string;

  @Column({ unique: true })
  email: string;

  @Column()
  @Exclude()
  password: string;

  @Column({ type: 'date' })
  birthDate: Date;

  @Column()
  gender: string;

  @Column({ unique: true })
  phone: string;

  @Column({ type: 'enum', enum: UserRoles })
  role: UserRoles;

  @Column({ type: 'boolean', default: false })
  isDeafaultPassword: boolean;

  @Column({ nullable: true })
  provider: string;
}

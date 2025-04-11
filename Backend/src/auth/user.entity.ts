import { Exclude } from 'class-transformer';
import { UserRoles } from 'src/common/types/user-roles';
import { Column, Entity, PrimaryGeneratedColumn } from 'typeorm';

@Entity()
export class User {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column()
  name: string;

  @Column()
  surname: string;

  @Column()
  email: string;

  @Column()
  @Exclude()
  password: string;

  @Column({ type: 'date' })
  birthDate: Date;

  @Column()
  gender: string;

  @Column()
  phone: string;

  @Column({ type: 'enum', enum: UserRoles })
  role: UserRoles;
}

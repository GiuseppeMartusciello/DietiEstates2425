import { Exclude } from 'class-transformer';
import { Provider } from 'src/common/types/provider.enum';
import { UserRoles } from 'src/common/types/user-roles';
import { UserNotification } from 'src/notification/user-notification.entity';
import { Notification } from 'src/notification/notification.entity';
import { Column, Entity, OneToMany, PrimaryGeneratedColumn } from 'typeorm';

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

  @Column({ type: 'date', nullable: true })
  birthDate: Date;

  @Column({ nullable: true })
  gender: string;

  @Column({ unique: true, nullable: true })
  phone: string;

  @Column({ type: 'enum', enum: UserRoles })
  role: UserRoles;

  @Exclude()
  @Column({ type: 'enum', enum: Provider, nullable: true })
  provider: Provider;

  @Column({ type: 'timestamp', nullable: true })
  lastPasswordChangeAt: Date;

  @OneToMany(
    () => UserNotification,
    (userNotification) => userNotification.notification,
  )
  userNotifications: UserNotification[];

  @OneToMany(() => Notification, (notification: Notification) => notification.createdBy)
  createdNotifications: Notification[];
  
}

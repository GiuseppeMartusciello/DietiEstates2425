import { User } from "src/auth/user.entity";
import { Notification } from './notification.entity';
import { Column, Entity, ManyToOne, PrimaryGeneratedColumn } from "typeorm";

@Entity()
export class UserNotification {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @ManyToOne(() => Notification, (notification) => notification.userNotifications, { onDelete: 'CASCADE' })
  notification: Notification;

  @ManyToOne(() => User, (user) => user.userNotifications, { onDelete: 'CASCADE' })
  user: User;

  @Column({ default: false })
  isRead: boolean;
}

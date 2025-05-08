import { Module } from '@nestjs/common';
import { Notification } from '../notification/notification.entity';
import { TypeOrmModule } from '@nestjs/typeorm';
import { UserNotification } from './user-notification.entity';

@Module({
    imports: [TypeOrmModule.forFeature([Notification,UserNotification])], // Add your entities here
    controllers: [],
    providers: [],
})
export class NotificationModule {}

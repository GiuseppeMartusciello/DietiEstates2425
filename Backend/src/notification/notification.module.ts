import { Module } from '@nestjs/common';
import { Notification } from '../notification/notification.entity';
import { TypeOrmModule } from '@nestjs/typeorm';
import { UserNotification } from './user-notification.entity';
import { NotificationService } from './notification.service';
import { NotificationController } from './notification.controller';
import { PushNotificationService } from './push-notification.service';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { Client } from 'src/client/client.entity';



@Module({
    imports: [ConfigModule, TypeOrmModule.forFeature([Notification,UserNotification,Client])], 
    controllers: [NotificationController],
    providers: [NotificationService,PushNotificationService],
    exports: [NotificationService,PushNotificationService],
})
export class NotificationModule {}

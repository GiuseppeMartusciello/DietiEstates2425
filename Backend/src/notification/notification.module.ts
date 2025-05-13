import { Module } from '@nestjs/common';
import { Notification } from '../notification/notification.entity';
import { TypeOrmModule } from '@nestjs/typeorm';
import { UserNotification } from './user-notification.entity';
import { NotificationService } from './notification.service';
import { NotificationController } from './notification.controller';
import { PushNotificationService } from './push-notifications/push-notification.service';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { Client } from 'src/client/client.entity';



@Module({
    imports: [ConfigModule, TypeOrmModule.forFeature([Notification,UserNotification,Client])], 
    controllers: [NotificationController],
    providers: [NotificationService,PushNotificationService],
    exports: [NotificationService,PushNotificationService],
})
export class NotificationModule {}


// in questo modulo sono presenti due entità: Notification e UserNotification
// Notification rappresenta la notifica vera e propria, 
// mentre UserNotification rappresenta la relazione tra un utente e una notifica

// sono stati inseriti nella stessa cartella poiché sono strettamente correlate
// e condividono la stessa logica di business
// in questo modo si evita di creare troppe cartelle e si mantiene il codice più organizzato
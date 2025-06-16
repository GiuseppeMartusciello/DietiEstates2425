import { Module } from '@nestjs/common';
import { OfferController } from './property-offer.controller';
import { OfferService } from './property-offer.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { PropertyOffer } from './property_offer.entity';
import { Client } from 'src/client/client.entity';
import { ListingRepository } from 'src/listing/listing.repository';
import { Listing } from 'src/listing/Listing.entity';
import { GeoapifyService } from 'src/common/services/geopify.service';
import { NotificationService } from 'src/notification/notification.service';
import { NotificationModule } from 'src/notification/notification.module';
import { Notification } from 'src/notification/notification.entity';
import { UserNotification } from 'src/notification/user-notification.entity';
import { ListingService } from 'src/listing/listing.service';
import { User } from 'src/auth/user.entity';

@Module({
  imports: [
    TypeOrmModule.forFeature([
      PropertyOffer,
      Client,
      Listing,
      Notification,
      UserNotification,
      User,
    ]),
    NotificationModule,
  ],
  controllers: [OfferController],
  providers: [
    OfferService,
    ListingRepository,
    GeoapifyService,
    NotificationService,
    ListingService,
  ],
  exports: [],
})
export class PropertyOfferModule {}

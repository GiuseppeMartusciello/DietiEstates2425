import { Module } from '@nestjs/common';
import { OfferController } from './property-offer.controller';
import { OfferService } from './property-offer.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { PropertyOffer } from './property_offer.entity';
import { Client } from 'src/client/client.entity';
import { ListingRepository } from 'src/listing/listing.repository';
import { Listing } from 'src/listing/Listing.entity';
import { GeoapifyService } from 'src/common/services/geopify.service';

@Module({
  imports: [TypeOrmModule.forFeature([PropertyOffer, Client, Listing])],
  controllers: [OfferController],
  providers: [OfferService, ListingRepository, GeoapifyService],
  exports: [],
})
export class PropertyOfferModule {}

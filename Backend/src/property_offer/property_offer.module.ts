import { Module } from '@nestjs/common';
import { ListingController } from 'src/listing/listing.controller';
import { OfferController } from './offer.controller';
import { OfferService } from './offer.service';
import { Type } from 'class-transformer';
import { TypeOrmModule } from '@nestjs/typeorm';
import { PropertyOffer } from './property_offer.entity';
import { OfferRepository } from './offer.repository';

@Module({
    imports: [TypeOrmModule.forFeature([PropertyOffer])],
    controllers: [OfferController],
    providers: [OfferService, OfferRepository],
    exports: [],
})
export class PropertyOfferModule {}

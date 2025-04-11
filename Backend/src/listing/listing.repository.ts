import { Injectable, NotFoundException } from '@nestjs/common';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { Listing } from './Listing.entity';
import { CreateListingDto } from './dto/create-listing.dto';
import { Agent } from 'src/agent/agent.entity';

@Injectable()
export class ListingRepository extends Repository<Listing> {
    
    constructor(@InjectRepository(Listing) private readonly repository: Repository<Listing>) {
        super(repository.target, repository.manager, repository.queryRunner);
    }

    async createListing(createListingDto: CreateListingDto, agent: Agent, position: string): Promise<Listing>{
        const {
            address,
            comune,
            city,
            postalCode,
            province,
            size,
            numberOfRooms,
            energyClass,
            nearbyPlaces,
            description,
            price,
            category,
            floor,
            hasElevator,
            hasAirConditioning,
            hasGarage,
            agentId,
          } = createListingDto;
          

        const listing = this.create({
            address,
            comune,
            city,
            postalCode,
            province,
            size,
            position,
            numberOfRooms,
            energyClass,
            nearbyPlaces,
            description,
            price,
            category,
            floor,
            hasElevator,
            hasAirConditioning,
            hasGarage,
            agency: agent.agency, // da sostituire con l'agente
            //agent,
        });

        await this.save(listing);
        return listing;
    }
}
import { Injectable } from '@nestjs/common';
import { ListingRepository } from './listing.repository';
import { CreateListingDto } from './dto/create-listing.dto';
import { Listing } from './Listing.entity';
import { Agent } from 'src/agent/agent.entity';
import { GeoapifyService } from 'src/common/services/geopify.service';

@Injectable()
export class ListingService {
  constructor(private readonly listingRepository: ListingRepository, private readonly geopifyService: GeoapifyService,) {}


    async createListing(
        createListingDto: CreateListingDto,
        agent: Agent,
    ): Promise<Listing>{
        
        const { lat, lon } = await this.geopifyService.getCoordinatesFromAddress(createListingDto.address);
        //const indicators = await this.geopifyService.getNearbyIndicators(lon,lat);  DA FINIRE
        console.log(`Lat ${lat}, Lon ${lon}`);
        //console.log("Indicatori: ", indicators);  DA FINIRE
        return this.listingRepository.createListing(createListingDto,agent,`${lat},${lon}`);
    }
}

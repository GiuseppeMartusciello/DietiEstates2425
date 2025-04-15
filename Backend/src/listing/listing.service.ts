import { Injectable, NotFoundException } from '@nestjs/common';
import { ListingRepository } from './listing.repository';
import { CreateListingDto } from './dto/create-listing.dto';
import { Listing } from './Listing.entity';
import { Agent } from 'src/agent/agent.entity';
import { GeoapifyService } from 'src/common/services/geopify.service';
import { Agency } from 'src/agency/agency.entity';
import { ModifyListingDto } from './dto/modify-listing.dto';
import { SearchListingDto } from './dto/search-listing.dto';

@Injectable()
export class ListingService {
  constructor(
    private readonly listingRepository: ListingRepository,
    private readonly geopifyService: GeoapifyService,
  ) {}


  searchListing(searchListingDto: SearchListingDto): Promise<Listing[]>{
    return this.listingRepository.searchListings(searchListingDto);
  }

  async getAllListing(): Promise<Listing[]> {
    const found = await this.listingRepository.find();

    return found;
  }
  
  async getListingByAgentId(agentId: string): Promise<Listing[]> {
    const found = await this.listingRepository.find({
      where: {
        agent: { userId: agentId } as Agent,
      },
    });

    if (found.length === 0) throw new NotFoundException();

    return found;
  }

  async getListingByAgencyId(agencyId: string): Promise<Listing[]> {
    const found = await this.listingRepository.find({
      where: {
        agency: { id: agencyId } as Agency,
      },
    });

    if (found.length === 0) throw new NotFoundException();

    return found;
  }

  async getListingById(id: string): Promise<Listing> {
    const found = await this.listingRepository.findOneBy({ id: id });

    if (!found) throw new NotFoundException(`Agent id  "${id}" not found`);

    return found;
  }

  async changeListing(
    listing: Listing,
    modifyListingDto: ModifyListingDto,
  ): Promise<Listing> {
    if (listing.address !== modifyListingDto.address) {
      const { lat, lon } = await this.geopifyService.getCoordinatesFromAddress(
        `${modifyListingDto.address}, ${modifyListingDto.municipality}`,
      );
      modifyListingDto.nearbyPlaces = await this.geopifyService.getNearbyIndicators(lat, lon);
      
      modifyListingDto.position = `${lat},${lon}`;
    }

    return this.listingRepository.modifyListing(listing, modifyListingDto);
  }

  async createListing(
    createListingDto: CreateListingDto,
    agent: Agent,
  ): Promise<Listing> {
    const { lat, lon } = await this.geopifyService.getCoordinatesFromAddress(
      `${createListingDto.address}, ${createListingDto.municipality}`,
    );
    createListingDto.nearbyPlaces =
      await this.geopifyService.getNearbyIndicators(lat, lon);
    createListingDto.position = `${lat},${lon}`;

    return this.listingRepository.createListing(createListingDto, agent);
  }

  async deleteListingById(id: string): Promise<void> {
    const result = await this.listingRepository.delete(id);
    if (result.affected === 0) {
      throw new NotFoundException(`Listing with ID "${id}" not found`);
    }
  }
}

import { Injectable, NotFoundException } from '@nestjs/common';
import { ListingRepository } from './listing.repository';
import { CreateListingDto } from './dto/create-listing.dto';
import { Listing } from './Listing.entity';
import { Agent } from 'src/agent/agent.entity';
import { GeoapifyService } from 'src/common/services/geopify.service';
import { Agency } from 'src/agency/agency.entity';
import { ModifyListingDto } from './dto/modify-listing.dto';
import { SearchListingDto } from './dto/search-listing.dto';
import * as pathModule from 'path';
import * as fs from 'fs';


@Injectable()
export class ListingService {
  constructor(
    private readonly listingRepository: ListingRepository,
    private readonly geopifyService: GeoapifyService,
  ) {}

  async getListingByAgentId(
    agentId: string,
    agencyId: string,
  ): Promise<Listing[]> {
    const found = await this.listingRepository.find({
      where: {
        agent: { userId: agentId } as Agent,
        agency: { id: agencyId } as Agency,
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

    if (!found) throw new NotFoundException(`Listing id  "${id}" not found`);

    return found;
  }

  async getAllListing(): Promise<Listing[]> {
    const found = await this.listingRepository.find();

    return found;
  }

  searchListing(searchListingDto: SearchListingDto): Promise<Listing[]> {
    return this.listingRepository.searchListings(searchListingDto);
  }

  async changeListing(
    listing: Listing,
    modifyListingDto: ModifyListingDto,
  ): Promise<Listing> {
    if (  //se passo le coordinate voglio che i posti vicini vengano calcolati su quelle nuove quindi non mi baso sull'indirizzo
      (modifyListingDto.latitude && modifyListingDto.latitude != listing.latitude) ||
      (modifyListingDto.longitude && modifyListingDto.longitude != listing.longitude)
    ) {
      const nearbyPlaces = await this.geopifyService.getNearbyIndicators(modifyListingDto.latitude, modifyListingDto.longitude);
      return this.listingRepository.modifyListing(listing, modifyListingDto,nearbyPlaces);
    }
      //se invece non passo le coordinate ma l'indirizzo allora ricalcolo le coordinate sulla base dell'indirizzo e
      //aggiorno i posti vicini
    else if (
      modifyListingDto.address &&
      listing.address !== modifyListingDto.address
    ) {
      const { lat, lon } = await this.geopifyService.getCoordinatesFromAddress(
        `${modifyListingDto.address}, ${modifyListingDto.municipality}`,
      );
      const nearbyPlaces = await this.geopifyService.getNearbyIndicators(
        lat,
        lon,
      );

      modifyListingDto.latitude = lat;
      modifyListingDto.longitude = lon;

      return this.listingRepository.modifyListing(listing, modifyListingDto,nearbyPlaces);
    }


    return this.listingRepository.modifyListing(listing, modifyListingDto);
  }

  async createListing(
    createListingDto: CreateListingDto,
    agent: Agent,
  ): Promise<Listing> {
    if (
      //se non vengono passate le coordinate le recupero da geopify, in caso contrario uso quelle passate
      createListingDto.latitude === undefined ||
      createListingDto.longitude === undefined
    ) {
      const { lat, lon } = await this.geopifyService.getCoordinatesFromAddress(
        `${createListingDto.address}, ${createListingDto.municipality}`,
      );
      createListingDto.latitude = lat;
      createListingDto.longitude = lon;
    }

    const nearbyPlaces = await this.geopifyService.getNearbyIndicators(
      createListingDto.latitude,
      createListingDto.longitude,
    );

    return this.listingRepository.createListing(
      createListingDto,
      agent.userId,
      agent.agency.id,
      nearbyPlaces,
    );
  }

  async deleteListingById(
    listingId: string,
    agencyId: string,
    agentId?: string,
  ): Promise<void> {
    const result = await this.listingRepository.delete({
      id: listingId,
      ...(agentId && { agent: { userId: agentId } as Agent }),
      agency: { id: agencyId } as Agency,
    });

    if (result.affected === 0) {
      throw new NotFoundException(`Listing with ID "${listingId}" not found`);
    }
  }

  async handleUploadedImages(listingId: string, files: Express.Multer.File[]) {
    if (!files || files.length === 0) {
      throw new NotFoundException('No images uploaded');
    }

    // Restituisco i path delle immagini relative
    return files.map((file) => ({
      filename: file.filename,
      path: `/uploads/${listingId}/${file.filename}`,
    }));
  }

  async getImagesForListing(listingId: string): Promise<string[]> {
    const imageDir = pathModule.join(__dirname, '..', '..', 'uploads', listingId);

    if (!fs.existsSync(imageDir)) {
      throw new NotFoundException('No images found for this listing');
    }
  
    return fs.readdirSync(imageDir).map((file) => `/uploads/${listingId}/${file}`);
  }
}

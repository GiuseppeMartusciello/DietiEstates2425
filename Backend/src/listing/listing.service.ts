import {
  BadRequestException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { ListingRepository } from './listing.repository';
import { CreateListingDto } from './dto/create-listing.dto';
import { Listing } from './Listing.entity';
import { Agent } from 'src/agent/agent.entity';
import { GeoapifyService } from 'src/common/services/geopify.service';
import { Agency } from 'src/agency/agency.entity';
import { ModifyListingDto } from './dto/modify-listing.dto';
import { SearchListingDto } from './dto/search-listing.dto';
import path, * as pathModule from 'path';
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

    return found;
  }

  async getListingByAgencyId(agencyId: string): Promise<Listing[]> {
    const found = await this.listingRepository.find({
      where: {
        agency: { id: agencyId } as Agency,
      },
    });

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
    //se ricevo l'indirizzo ed è diverso allora ricalcolo le coordinate e aggiorno i posti vicini
    if (
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

      return this.listingRepository.modifyListing(
        listing,
        modifyListingDto,
        nearbyPlaces,
        lat,
        lon,
      );
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

    const nearbyPlaces = await this.geopifyService.getNearbyIndicators(
      lat,
      lon,
    );

    return this.listingRepository.createListing(
      createListingDto,
      agent.userId,
      agent.agency.id,
      nearbyPlaces,
      lat,
      lon,
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
      throw new BadRequestException(`Listing with ID "${listingId}" not found`);
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
    const imageDir = pathModule.join(
      __dirname,
      '..',
      '..',
      'uploads',
      listingId,
    );

    if (!fs.existsSync(imageDir)) {
      throw new NotFoundException('No images found for this listing');
    }

    return fs
      .readdirSync(imageDir)
      .map((file) => `/uploads/${listingId}/${file}`);
  }

  async getAllListingImages(): Promise<Record<string, string[]>> {
    const uploadsDir = pathModule.join(__dirname, '..', '..', 'uploads');
    const results: Record<string, string[]> = {};

    if (!fs.existsSync(uploadsDir)) {
      return results;
    }

    const listingFolders = fs.readdirSync(uploadsDir);

    for (const listingId of listingFolders) {
      const imageDir = pathModule.join(uploadsDir, listingId);

      if (fs.statSync(imageDir).isDirectory()) {
        try {
          const images = await this.getImagesForListing(listingId);
          results[listingId] = images;
        } catch (e) {
          results[listingId] = [];
        }
      }
    }

    return results;
  }

  async deleteListingImage(
    listingId: string,
    imageFilename: string,
  ): Promise<{ success: boolean }> {
    const filePath = pathModule.join(
      __dirname,
      '..',
      '..',
      'uploads',
      listingId,
      imageFilename,
    );

    if (!fs.existsSync(filePath)) {
      throw new NotFoundException('Immagine non trovata');
    }

    fs.unlinkSync(filePath);

    return { success: true };
  }
}

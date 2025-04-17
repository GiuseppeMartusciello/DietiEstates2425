import { Injectable, NotFoundException } from '@nestjs/common';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { Listing } from './Listing.entity';
import { CreateListingDto } from './dto/create-listing.dto';
import { ModifyListingDto } from './dto/modify-listing.dto';
import { SearchListingDto } from './dto/search-listing.dto';
import { GeoapifyService } from 'src/common/services/geopify.service';
import { SearchType } from 'src/common/types/searchType.enum';

@Injectable()
export class ListingRepository extends Repository<Listing> {
  constructor(
    private readonly geopifyService: GeoapifyService,
    @InjectRepository(Listing) private readonly repository: Repository<Listing>,
  ) {
    super(repository.target, repository.manager, repository.queryRunner);
  }

  async searchListings(serchListingDto: SearchListingDto): Promise<Listing[]> {
    const {
      searchType,
      municipality,
      latitude,
      longitude,
      radius,
      minPrice,
      maxPrice,
      numberOfRooms,
      category,
      minSize,
      energyClass,
      hasElevator,
      hasAirConditioning,
      hasGarage,
    } = serchListingDto;

    const query = this.createQueryBuilder('listing');

    if (minPrice !== undefined)
      query.andWhere('listing.price >= :minPrice', { minPrice });

    if (maxPrice !== undefined)
      query.andWhere('listing.price <= :maxPrice', { maxPrice });

    if (numberOfRooms !== undefined)
      query.andWhere('listing.numberOfRooms >= :numberOfRooms', {
        numberOfRooms,
      });

    if (category !== undefined)
      query.andWhere('listing.category = :category', { category });

    if (minSize !== undefined)
      query.andWhere('listing.size >= :minSize', { minSize });

    if (energyClass !== undefined)
      //al momento controlla che siano uguali, sarebbe meglio controllare che sia >= ?
      query.andWhere('listing.energyClass = :energyClass', { energyClass });

    if (hasElevator !== undefined && hasElevator)
      query.andWhere('listing.hasElevator = true');

    if (hasAirConditioning !== undefined && hasAirConditioning)
      query.andWhere('listing.hasAirConditioning = true');

    if (hasGarage !== undefined && hasGarage)
      query.andWhere('listing.hasGarage = true');

    if (searchType === SearchType.MUNICIPALITY) {
      query.andWhere('listing.municipality = :municipality', { municipality });
      return await query.getMany();
    }

    // Se la ricerca è per raggio allora continuo
    const listings = await query.getMany();

    if (
      latitude !== undefined &&
      longitude !== undefined &&
      radius !== undefined
    ) {
      return listings.filter((listing) => {
        const dist = this.geopifyService.calculateDistance(
          latitude,
          longitude,
          listing.latitude,
          listing.longitude,
        );
        return dist <= radius;
      });
    }

    return listings;
  }

  async modifyListing(
    listing: Listing,
    modifyListingDto: ModifyListingDto,
    nearbyPlaces?: string[],
  ): Promise<Listing> {
    const updatableFields: (keyof ModifyListingDto)[] = [
      'address',
      'title',
      'municipality',
      'city',
      'postalCode',
      'province',
      'size',
      'numberOfRooms',
      'energyClass',
      'latitude',
      'longitude',
      'description',
      'price',
      'category',
      'floor',
      'hasElevator',
      'hasAirConditioning',
      'hasGarage',
    ];

    updatableFields.forEach((field) => {
      if (modifyListingDto[field] !== undefined) {
        (listing as any)[field] = modifyListingDto[field];
      }
    });

    if(nearbyPlaces && listing.nearbyPlaces != nearbyPlaces)
      listing.nearbyPlaces= nearbyPlaces;

    await this.save(listing);
    return listing;
  }

  async createListing(
    createListingDto: CreateListingDto,
    agentId: string,
    agencyId: string,
    nearbyPlaces: string[],
  ): Promise<Listing> {
    const {
      address,
      title,
      municipality,
      city,
      postalCode,
      province,
      size,
      latitude,
      longitude,
      numberOfRooms,
      energyClass,
      description,
      price,
      category,
      floor,
      hasElevator,
      hasAirConditioning,
      hasGarage,
    } = createListingDto;

    const position = `${latitude},${longitude}`;
    console.log('Indicatori: ', nearbyPlaces);
    const listing = this.create({
      address,
      municipality,
      title,
      city,
      postalCode,
      province,
      size,
      latitude,
      longitude,
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
      agency: { id: agencyId },
      agent: { userId: agentId },
    });

    await this.save(listing);
    return listing;
  }
}

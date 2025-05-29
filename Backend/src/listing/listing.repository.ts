import { Injectable } from '@nestjs/common';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { Listing } from './Listing.entity';
import { CreateListingDto } from './dto/create-listing.dto';
import { ModifyListingDto } from './dto/modify-listing.dto';
import { GeoapifyService } from 'src/common/services/geopify.service';
import { SearchType } from 'src/common/types/searchType.enum';
import { ResearchListingDto } from 'src/research/dto/create-research.dto';

@Injectable()
export class ListingRepository extends Repository<Listing> {
  constructor(
    private readonly geopifyService: GeoapifyService,
    @InjectRepository(Listing) private readonly repository: Repository<Listing>,
  ) {
    super(repository.target, repository.manager, repository.queryRunner);
  }

  async searchListings(
  serchListingDto: ResearchListingDto,
): Promise<Listing[]> {
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

  //todo si dovrebbe aggiungere anche provincia tra i filtri
  const energyRank: Record<string, number> = {
    A: 5,
    B: 6,
    C: 7,
    D: 8,
    E: 9,
    F: 10,
    G: 11,
  };

  const query = this.createQueryBuilder('listing');

  if (minPrice !== undefined)
    query.andWhere('listing.price >= :minPrice', { minPrice });

  if (maxPrice !== undefined)
    query.andWhere('listing.price <= :maxPrice', { maxPrice });

  if (numberOfRooms !== undefined)
    query.andWhere('listing.numberOfRooms >= :numberOfRooms', { numberOfRooms });

  if (category !== undefined)
    query.andWhere('listing.category = :category', { category });

  if (minSize !== undefined)
      query.andWhere('CAST(listing.size AS INTEGER) >= :minSize', { minSize });

  if (hasElevator) query.andWhere('listing.hasElevator = true');
  if (hasAirConditioning) query.andWhere('listing.hasAirConditioning = true');
  if (hasGarage) query.andWhere('listing.hasGarage = true');

  let listings = await query.getMany();

  //  Energy class filter
  if (energyClass && energyRank[energyClass] !== undefined) {
    const threshold = energyRank[energyClass];
    listings = listings.filter(
      (listing) =>
        listing.energyClass &&
        energyRank[listing.energyClass] !== undefined &&
        energyRank[listing.energyClass] <= threshold
    );
  }

  //  Municipality match
  if (searchType === SearchType.MUNICIPALITY && municipality?.trim()) {
    listings = listings.filter((listing) =>
      listing.municipality?.toLowerCase().includes(municipality.toLowerCase())
    );
  }

  //  Coordinates (radius) match
  if (
    searchType === SearchType.COORDINATES &&
    latitude !== undefined &&
    longitude !== undefined &&
    radius !== undefined
  ) {
    listings = listings.filter((listing) => {
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
    latitude?: number,
    longitude?: number,
  ): Promise<Listing> {
    const updatableFields: (keyof ModifyListingDto)[] = [
      'address',
      'title',
      'municipality',
      'postalCode',
      'province',
      'size',
      'numberOfRooms',
      'energyClass',
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

    if (latitude !== undefined) listing.latitude = latitude;

    if (longitude !== undefined) listing.longitude = longitude;

    if (nearbyPlaces && listing.nearbyPlaces != nearbyPlaces)
      listing.nearbyPlaces = nearbyPlaces;

    await this.save(listing);
    return listing;
  }

  async createListing(
    createListingDto: CreateListingDto,
    agentId: string,
    agencyId: string,
    nearbyPlaces: string[],
    latitude?: number,
    longitude?: number,
  ): Promise<Listing> {
    const {
      address,
      title,
      municipality,
      postalCode,
      province,
      size,
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

    const listing = this.create({
      address,
      municipality,
      title,
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

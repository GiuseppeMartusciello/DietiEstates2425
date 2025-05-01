import { Injectable } from '@nestjs/common';
import { Repository } from 'typeorm';
import { Client } from './client.entity';

@Injectable()
export class ClientRepository extends Repository<Client> {
  constructor(private readonly clientRepository: Repository<Client>) {
    super(
      clientRepository.target,
      clientRepository.manager,
      clientRepository.queryRunner,
    );
  }

  async findClientByListingId(listingId: string): Promise<Client[]> {
    const clients = await this.createQueryBuilder('client')
      .innerJoinAndSelect('client.propertyOffers', 'propertyOffer')
      .innerJoin('propertyOffer.listing', 'listing')
      .where('listing.id = :listingId', { listingId })
      .distinct(true)
      .getMany();

    return clients;
  }

}

import { Inject, Injectable, NotFoundException } from '@nestjs/common';
import { Research } from './research.entity';
import { ResearchListingDto } from './dto/create-research.dto';
import { Client } from 'src/client/client.entity';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { ListingRepository } from 'src/listing/listing.repository';
import { Listing } from 'src/listing/Listing.entity';


@Injectable()
export class ResearchService {
  constructor(
    @InjectRepository(Research)
    private readonly researchRepository: Repository<Research>,

    @Inject(ListingRepository)
    private readonly listingRepository: ListingRepository,
  ) {}

  //Restituisce le ricercehe fatte da un cliente
  async getResearchByClientId(userId: string): Promise<Research[]> {

    const found = await this.researchRepository.find({
      where: { client: { userId: userId } },
      order: { date: 'DESC' },
    });
    
    return found;
  }

  //elimina ricerca
  async deleteResearch(id: string, client: Client): Promise<void> {
    const result = await this.researchRepository.delete({ id, client });

    if (!result) {
      throw new NotFoundException(`Task with ID "${id}" not found`);
    }
  }

  //crea una ricerca effettuata da un utente
  async createResearch(
    researchListingDto: ResearchListingDto,
    client: Client,
  ): Promise<Listing[]> {

    const newresearch = this.researchRepository.create({
      ...researchListingDto,
      date: new Date(),
      client,
    });
    await  this.researchRepository.save(newresearch);

    const result = await this.listingRepository.searchListings(researchListingDto);

    
    return result;
  }

  //restituisce le ultime 10 ricerche effettuate da un cliente
  //questo metodo è usato per la barra di ricerca
  async getLast10ResearchByClientId(userId: string): Promise<Research[]> {
    const found = await this.researchRepository.find({
      where: { client: { userId: userId } },
      order: { date: 'DESC' },
      take: 10,
    });

    return found;
  }

  //Aggiorna la data di una ricerca quando viene effettuata una seconda volta
  async updateResearch(researchId: string, client: Client): Promise<Research> {
    const research = await this.researchRepository.findOne({
      where: { id: researchId, client },
    });
    if (!research) {
      throw new NotFoundException(`Research with ID "${researchId}" not found`);
    }

    research.date = new Date();
    await this.researchRepository.save(research);

    return research;
  }
}

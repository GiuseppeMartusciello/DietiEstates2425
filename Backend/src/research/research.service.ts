import { Injectable, NotFoundException } from '@nestjs/common';
import { Research } from './research.entity';
import { CreateResearchDto } from './dto/create-research.dto';
import { Client } from 'src/client/client.entity';
import { ResearchRepository } from './research.repository';
import { InjectRepository } from '@nestjs/typeorm';
import { Listing } from 'src/listing/Listing.entity';
import { ListingRepository } from 'src/listing/listing.repository';



@Injectable()
export class ResearchService {
 
  constructor(
    private readonly researchRepository: ResearchRepository,

    @InjectRepository(Listing)
    private readonly listingRepository: ListingRepository
  ) {}
//Restituisce le ricercehe fatte da un cliente
  async getResearchByClientId(userId: string): Promise<Research[]> {
    const found = await this.researchRepository.find({
      where: { client: { userId: userId } },
      order: { date: 'DESC' },
    });
          
  if(found.length === 0)
      throw new NotFoundException(`No research associated with id  "${userId}" not found`);
          
    return found;
  }

//elimina ricerca 
  async deleteResearch(id: string, client: Client): Promise<void> {
    const result = await this.researchRepository.delete({ id, client });

    if (result.affected === 0) {
      throw new NotFoundException(`Task with ID "${id}" not found`);
    }
  }


//crea una ricerca ed Effetta effettivamente la ricerca
//restituisce gli immobili che soddisfano i criteri di ricerca
//la parte della query sui listing non è testata e deve essere finita di ideata e implementata
  async createResearch(
    createResearchDto: CreateResearchDto,
    client: Client,
  ): Promise<Listing[]> {

    const { text, municipality,latitude,longitude,radius} = createResearchDto;

      const newresearch = this.researchRepository.create({
            municipality,
            latitude,
            longitude,
            radius,
            date: new Date(),
            text: text,
            client,
        })

      await this.researchRepository.save(newresearch);
      
    
      if(municipality === null) {
        const listings = await this.listingRepository.find({
          where: {longitude: longitude, latitude: latitude},
        });  
        return listings;
      }
      else {
        const listings = await this.listingRepository.find({
          where: { municipality: municipality },
        });
        return listings;
      }
      //const research = await this.listingRepository.find({}) bisogna effettivamente implementare la ricerca
  }


//restituisce le ultime 10 ricerche effettuate da un cliente
//questo metodo è usato per la barra di ricerca
  async getLast10ResearchByClientId(userId: string): Promise<Research[]> {

    const found = await this.researchRepository.find({
        where: { client: { userId: userId } },
        order: { date: 'DESC' },
        take: 10,
    });

    if (found.length === 0) {
        throw new NotFoundException(`No research associated with id "${userId}" not found`);
    }
    return found;
  }
//Aggiorna la data di una ricerca quando viene effettuata una seconda volta
  async updateResearch(researchId: string, client: Client): Promise<Research> {

    const research =  await this.researchRepository.findOne({ where: { id:researchId , client } })
    if(!research) {
      throw new NotFoundException(`Research with ID "${researchId}" not found`);
    }

    research.date = new Date();
    this.researchRepository.save(research);

    return research;
  }
}

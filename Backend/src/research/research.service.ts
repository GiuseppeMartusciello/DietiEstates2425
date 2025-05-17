import { Inject, Injectable, NotFoundException } from '@nestjs/common';
import { Research } from './research.entity';
import { CreateResearchDto } from './dto/create-research.dto';
import { Client } from 'src/client/client.entity';
import { ResearchRepository } from './research.repository';

@Injectable()
export class ResearchService {
 
  constructor(
    @Inject(ResearchRepository)
    private readonly researchRepository: ResearchRepository) {}



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


//crea una ricerca effettuata da un utente
  async createResearch(
    createResearchDto: CreateResearchDto,
    client: Client,
  ): Promise<Research> {

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
      
      return newresearch;

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

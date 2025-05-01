import { Injectable, NotFoundException } from '@nestjs/common';
import { Research } from './research.entity';
import { CreateResearchDto } from './dto/create-research.dto';
import { Client } from 'src/client/client.entity';
import { ResearchRepository } from './research.repository';



@Injectable()
export class ResearchService {
 
  constructor(private readonly researchRepository: ResearchRepository) {}

  async getResearchByClientId(userId: string): Promise<Research[]> {
    const found = await this.researchRepository.find({
      where: { client: { userId: userId } },
      order: { date: 'DESC' },
    });
          
  if(found.length === 0)
      throw new NotFoundException(`No research associated with id  "${userId}" not found`);
          
    return found;
  }


  async deleteResearch(id: string, client: Client): Promise<void> {
    const result = await this.researchRepository.delete({ id, client });

    if (result.affected === 0) {
      throw new NotFoundException(`Task with ID "${id}" not found`);
    }
  }

  async createResearch(
    createResearchDto: CreateResearchDto,
    client: Client,
  ): Promise<Research> {

    const { text, municipality, coordinates,radius} = createResearchDto;
        const research = this.researchRepository.create({
            municipality,
            coordinates,
            radius,
            date: new Date(),
            text: text,
            client,
        })

        await this.researchRepository.save(research);
        return research;
  }


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

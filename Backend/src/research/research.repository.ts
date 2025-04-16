import { Injectable, NotFoundException } from '@nestjs/common';
import { Repository } from 'typeorm';
import { Research } from './research.entity';
import { CreateResearchDto } from './dto/create-research.dto';
import { Client } from 'src/client/client.entity';
import { InjectRepository } from '@nestjs/typeorm';
import { Last10ResearchDto } from './dto/last-10-research.dto';
import { RepeatedSearchDto } from './dto/repeted-search.dto';

@Injectable()
export class ResearchRepository extends Repository<Research> {
   
  
    
    constructor(@InjectRepository(Research) private readonly repository: Repository<Research>) {
        
        super(repository.target, repository.manager, repository.queryRunner);
      }


    async getResearchByClientId(userId: string): Promise<Research[]>{
        const found = await this.find({
            where: { client: { userId: userId } },
            order: { date: 'DESC' },
        });
                
        if(found.length === 0)
            throw new NotFoundException(`No research associated with id  "${userId}" not found`);
                
        return found;
    }


    async createResearch(createResearchDto: CreateResearchDto, client: Client): Promise<Research>{

        const { text, municipality, coordinates,radius} = createResearchDto;
        const research = this.create({
            municipality,
            coordinates,
            radius,
            date: new Date(),
            text: text,
            client,
        })

        await this.save(research);
        return research;
    }

    async getLast10ResearchByClientId(last10ResearchDto: Last10ResearchDto): Promise<Research[]> {


            const { id } = last10ResearchDto;
            const found = await this.find({
                where: { client: { userId: id } },
                order: { date: 'DESC' },
                take: 10,
            });

            if (found.length === 0) {
                throw new NotFoundException(`No research associated with id "${id}" not found`);
            }
            return found;
      }

      async updateResearch(repeatedSearch: RepeatedSearchDto, client: Client): Promise<void> {
        
        const { id , date } = repeatedSearch;
        await this.update({ id, client }, { date: new Date() });
      }
}
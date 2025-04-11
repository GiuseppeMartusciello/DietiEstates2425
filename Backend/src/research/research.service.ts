import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Research } from './research.entity';
import { Repository } from 'typeorm';
import { CreateResearchDto } from './create-research.dto';
import { Client } from 'src/client/client.entity';
import { ResearchRepository } from './research.repository';

@Injectable()
export class ResearchService {
    constructor(private readonly researchRepository: ResearchRepository,
    ){}
    

    async getResearchById(id: string): Promise<Research>{
        const found = await this.researchRepository.findOneBy({ id });

        if(!found)
            throw new NotFoundException(`Research id  "${id}" not found`);
        
        return found;
    }

    async getResearchByClientId(userId: string): Promise<Research[]>{
        const found = await this.researchRepository.find({
            where: {
              client: {
                user: {
                  id: userId,
                },
              },
            },
            relations: ['client', 'client.user'], // assicurati che queste relazioni siano caricate
            order: { date: 'DESC' }, // opzionale
          });
        
        
        if(!found)
            throw new NotFoundException(`No research associated with id  "${userId}" not found`);
        
        return found;
    }
    

    createResearch(createResearchDto: CreateResearchDto, client: Client): Promise<Research> {
        return this.researchRepository.createResearch(createResearchDto,client);
    }


}

import { Injectable, NotFoundException } from '@nestjs/common';
import { Repository } from 'typeorm';
import { Research } from './research.entity';
import { CreateResearchDto } from './create-research.dto';
import { Client } from 'src/client/client.entity';
import { InjectRepository } from '@nestjs/typeorm';

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

        const research = this.create({
            date: new Date(),
            text: createResearchDto.text,
            client,
        })

        await this.save(research);
        return research;
    }
}
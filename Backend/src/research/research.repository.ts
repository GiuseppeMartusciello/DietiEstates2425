import { Injectable } from '@nestjs/common';
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
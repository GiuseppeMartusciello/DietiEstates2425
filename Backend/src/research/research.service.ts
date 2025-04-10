import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Research } from './research.entity';
import { Repository } from 'typeorm';
import { CreateResearchDto } from './create-research.dto';

@Injectable()
export class ResearchService {
    constructor(
        @InjectRepository(Research)
        private readonly researchRepository: Repository<Research>,
    ){}
    

    async getResearchById(id: string): Promise<Research>{
        const found = await this.researchRepository.findOneBy({ id });

        if(!found)
            throw new NotFoundException(`Research id  "${id}" not found`);
        
        return found;
    }

//    async createResearch(createResearchDto: CreateResearchDto): Promise<Research>{
//        const { text } = createResearchDto;
//
//        const task = this.researchRepository.create({
//            text
//            
//        })
//    }
}

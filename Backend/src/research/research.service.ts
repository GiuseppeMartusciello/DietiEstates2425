import { Injectable, NotFoundException } from '@nestjs/common';
import { Research } from './research.entity';
import { CreateResearchDto } from './dto/create-research.dto';
import { Client } from 'src/client/client.entity';
import { ResearchRepository } from './research.repository';
import { Last10ResearchDto } from './dto/last-10-research.dto';
import { last } from 'rxjs';
import { RepeatedSearchDto } from './dto/repeted-search.dto';

@Injectable()
export class ResearchService {
 
  constructor(private readonly researchRepository: ResearchRepository) {}

  getResearchByClientId(userId: string): Promise<Research[]> {
    return this.researchRepository.getResearchByClientId(userId);
  }

  async deleteResearch(id: string, client: Client): Promise<void> {
    const result = await this.researchRepository.delete({ id, client });

    if (result.affected === 0) {
      throw new NotFoundException(`Task with ID "${id}" not found`);
    }
  }

  createResearch(
    createResearchDto: CreateResearchDto,
    client: Client,
  ): Promise<Research> {
    return this.researchRepository.createResearch(createResearchDto, client);
  }

  getLast10ResearchByClientId(last10ResearchDto: Last10ResearchDto): Promise<Research[]> {
    return this.researchRepository.getLast10ResearchByClientId(last10ResearchDto);
  }

  updateResearch(repeatedSearch: RepeatedSearchDto, client: Client): Promise<void> {
    return this.researchRepository.updateResearch(repeatedSearch, client);
  }
}

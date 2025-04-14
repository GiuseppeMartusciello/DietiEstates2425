import { Injectable, NotFoundException } from '@nestjs/common';
import { Research } from './research.entity';
import { CreateResearchDto } from './create-research.dto';
import { Client } from 'src/client/client.entity';
import { ResearchRepository } from './research.repository';

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
}

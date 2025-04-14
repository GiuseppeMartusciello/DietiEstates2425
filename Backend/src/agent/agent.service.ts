import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Agent } from './agent.entity';

@Injectable()
export class AgentService {
  constructor(
    @InjectRepository(Agent)
    private readonly agentRepository: Repository<Agent>,
  ) {}


    async getAgentById(id: string): Promise<Agent> {
      const found = await this.agentRepository.findOneBy({ userId: id });
  
      if (!found) throw new NotFoundException(`Agent id  "${id}" not found`);
  
      return found;
    }

    async findAgentByLicense(licenseNumber): Promise<Agent> {
      const found = await this.agentRepository.findOneBy({ licenseNumber });

      if (!found) throw new NotFoundException(`Agent with license "${licenseNumber}" not found`);

      return found;
    }
}

import { Injectable } from "@nestjs/common";
import { Repository } from "typeorm";
import { Agent } from "./agent.entity";

@Injectable()
export class AgentRepository extends Repository<Agent> {
  constructor(private readonly agentRepository: Repository<Agent>) {
    super(
      agentRepository.target,
      agentRepository.manager,
      agentRepository.queryRunner,
    );
  }
}

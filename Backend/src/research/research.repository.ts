import { Injectable, NotFoundException } from '@nestjs/common';
import { Repository } from 'typeorm';
import { Research } from './research.entity';
import { CreateResearchDto } from './dto/create-research.dto';
import { Client } from 'src/client/client.entity';
import { InjectRepository } from '@nestjs/typeorm';


@Injectable()
export class ResearchRepository extends Repository<Research> {
   
    constructor(@InjectRepository(Research) private readonly repository: Repository<Research>) {
        super(repository.target, repository.manager, repository.queryRunner);
      }
}
import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Client } from './client.entity';
import { Repository } from 'typeorm';

@Injectable()
export class ClientService {
  constructor(
    @InjectRepository(Client)
    private readonly clientRepository: Repository<Client>,
  ) {}

  async getClientById(id: string): Promise<Client> {
    const found = await this.clientRepository.findOneBy({ userId: id });

    if (!found) throw new NotFoundException(`Research id  "${id}" not found`);

    return found;
  }
}

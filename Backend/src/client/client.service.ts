import { BadRequestException, Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Client } from './client.entity';
import { Repository } from 'typeorm';
import { UpdateNotificationPreferenceDto } from './Dto/updateNotificationPreference.dto';

@Injectable()
export class ClientService {
  constructor(
    @InjectRepository(Client)
    private readonly clientRepository: Repository<Client>,
  ) {}

  async getClientById(id: string): Promise<Client> {
    const found = await this.clientRepository.findOneBy({ userId: id });

    if (!found) throw new NotFoundException(`Client id "${id}" not found`);

    return found;
  }

  async updateNotificationPreference(
    userId: string,
    dto: UpdateNotificationPreferenceDto,
  ): Promise<void> {
    const client = await this.clientRepository.findOne({ where: { userId } });
    if (!client) throw new NotFoundException('Client not found');

    switch (dto.type) {
      case 'promotional':
        client.promotionalNotification = dto.value;
        break;
      case 'offer':
        client.offerNotification = dto.value;
        break;
      case 'search':
        client.searchNotification = dto.value;
        break;
      default:
        throw new BadRequestException('Tipo di notifica non valido');
    }

    await this.clientRepository.save(client);
  }
}

import { Injectable } from '@nestjs/common';
import { Repository } from 'typeorm';
import { Manager } from './agency-manager.entity';

@Injectable()
export class ManagerRepository extends Repository<Manager> {
  constructor(private readonly managerRepository: Repository<Manager>) {
    super(
      managerRepository.target,
      managerRepository.manager,
      managerRepository.queryRunner,
    );
  }
}

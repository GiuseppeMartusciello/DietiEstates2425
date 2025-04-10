import { Repository } from 'typeorm';
import { SupportAdmin } from './support-admin.entity';

export class SupportAdminRepository extends Repository<SupportAdmin> {
  constructor(
    private readonly supportAdminRepository: Repository<SupportAdmin>,
  ) {
    super(
      supportAdminRepository.target,
      supportAdminRepository.manager,
      supportAdminRepository.queryRunner,
    );
  }
}

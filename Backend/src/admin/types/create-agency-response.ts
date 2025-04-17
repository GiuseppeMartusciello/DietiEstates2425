import { Agency } from 'src/agency/agency.entity';
import { UserItem } from 'src/common/types/userItem';

export interface CreateAgencyResponse {
  message: string;
  agency: Agency;
  manager: UserItem;
}

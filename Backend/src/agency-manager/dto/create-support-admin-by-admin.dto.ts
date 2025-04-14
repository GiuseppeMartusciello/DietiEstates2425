import { IsUUID } from 'class-validator';
import { CreateSupportAdminDto } from './create-support-admin.dto';

export class CreateSupportAdminByAdminDto extends CreateSupportAdminDto {
  @IsUUID()
  agencyId: string;
}

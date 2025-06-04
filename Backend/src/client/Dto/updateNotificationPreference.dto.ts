import { IsBoolean, IsIn } from "class-validator";

export class UpdateNotificationPreferenceDto {
  @IsIn(['promotional', 'offer', 'search'])
  type: 'promotional' | 'offer' | 'search';

  @IsBoolean()
  value: boolean;
}

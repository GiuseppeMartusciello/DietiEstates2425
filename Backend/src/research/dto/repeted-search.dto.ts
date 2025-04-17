import { Type } from 'class-transformer';
import { IsDate, IsNumber, IsOptional, IsString, IsUUID } from 'class-validator';

export class RepeatedSearchDto {
  @IsUUID()
  id: string;

  @IsDate()
  @Type(() => Date)
  date: Date;
}
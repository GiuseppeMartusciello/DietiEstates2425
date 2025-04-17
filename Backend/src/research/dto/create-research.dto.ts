import { IsOptional, IsString, IsNumber, IsDate, IsUUID, Validate } from 'class-validator';
import { Type } from 'class-transformer';
import { MunicipalityCoordinatesValidator } from '../../common/validator/municipality-coordinates.validator';


export class CreateResearchDto {
  @IsUUID()
  @IsOptional()
  id?: string;

  @IsString()
  @IsOptional()
  municipality?: string;

  @IsString()
  @IsOptional()
  coordinates?: string;

  @IsNumber()
  @IsOptional()
  radius?: number;

  @IsDate()
  @Type(() => Date)
  date: Date;

  @IsString()
  text: string;

  @Validate(MunicipalityCoordinatesValidator)
  dummyPropertyForCustomValidation: any; // workaround per attivare la validazione sulla classe
}

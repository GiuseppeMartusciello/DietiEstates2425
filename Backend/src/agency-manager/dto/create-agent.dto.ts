import { Type } from 'class-transformer';
import {
  IsDate,
  IsEmail,
  IsEnum,
  IsNotEmpty,
  IsPhoneNumber,
  IsString,
  IsStrongPassword,
  IsUUID,
} from 'class-validator';
import { Gender } from 'src/common/types/gender.enum';

export class CreateAgentDto {
  @IsUUID()
  licenseNumber: string;

  @IsNotEmpty()
  @IsString()
  name: string;

  @IsNotEmpty()
  @IsString()
  surname: string;

  @IsNotEmpty()
  @IsEmail()
  email: string;

  @IsNotEmpty()
  @IsStrongPassword()
  password: string;

  @IsDate()
  @Type(() => Date)
  birthDate: Date;

  @IsNotEmpty()
  @IsEnum(Gender)
  gender: string;

  @IsNotEmpty()
  @IsPhoneNumber()
  phone: string;

  @IsDate()
  @Type(() => Date)
  start_date: Date;

  @IsNotEmpty()
  languages: string[];

  // @IsUUID()
  // agencyId: string;
}

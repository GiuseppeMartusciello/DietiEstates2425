import { Type } from 'class-transformer';
import {
  IsNotEmpty,
  IsString,
  IsPhoneNumber,
  Length,
  IsEmail,
  IsDate,
  IsEnum,
} from 'class-validator';
import { Gender } from 'src/common/types/gender.enum';

export class CreateAgencyDto {
  @IsNotEmpty()
  @IsString()
  name: string;

  @IsNotEmpty()
  @IsString()
  legalAddress: string;

  @IsNotEmpty()
  @IsPhoneNumber()
  phone: string;

  @IsNotEmpty()
  @IsString()
  @Length(11, 11, { message: 'Vat number must be 11 characters long' })
  vatNumber: string;

  @IsNotEmpty()
  @IsString()
  managerName: string;

  @IsNotEmpty()
  @IsString()
  managerSurname: string;

  @IsNotEmpty()
  @IsEmail()
  managerEmail: string;

  @IsDate()
  @Type(() => Date)
  managerBirthDate: Date;

  @IsNotEmpty()
  @IsEnum(Gender)
  managerGender: string;

  @IsNotEmpty()
  @IsPhoneNumber()
  managerPhone: string;
}

import {
  IsNotEmpty,
  IsString,
  IsPhoneNumber,
  Length,
  IsEmail,
} from 'class-validator';

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

  @IsNotEmpty()
  @IsPhoneNumber()
  managerPhone: string;
}

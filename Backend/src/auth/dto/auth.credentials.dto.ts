import { Type } from 'class-transformer';
import {
  IsDate,
  IsEmail,
  IsEnum,
  IsNotEmpty,
  IsPhoneNumber,
  IsString,
  IsStrongPassword,
} from 'class-validator';
import { Gender } from 'src/common/types/gender.enum';

export class AuthCredentialDto {
  @IsNotEmpty()
  @IsString()
  name: string;

  @IsNotEmpty()
  @IsString()
  surname: string;

  @IsNotEmpty()
  @IsEmail()
  email: string;

  @IsStrongPassword()
  password: string;

  @IsNotEmpty()
  @IsPhoneNumber('IT')
  phone: string;

  // @IsDate()
  // @Type(() => Date)
  // birthDate: Date;

  // @IsNotEmpty()
  // @IsEnum(Gender)
  // gender: string;

  // @IsNotEmpty()
  // @IsString()
  // address: string;
}

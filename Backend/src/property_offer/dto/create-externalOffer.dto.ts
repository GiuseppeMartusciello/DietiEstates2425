import { IsEmail, IsNumber, IsOptional, IsString } from 'class-validator';

export class CreateExternalOfferDto {
  @IsNumber()
  price: number;

  @IsEmail()
  guestEmail: string;

  @IsString()
  guestName: string;

  @IsString()
  guestSurname: string;
}

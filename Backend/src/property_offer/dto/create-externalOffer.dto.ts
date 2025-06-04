import { IsEmail, IsNumber, IsOptional, IsString } from 'class-validator';

export class CreateExternalOfferDto {
  @IsNumber()
  price: number;

  @IsOptional()
  @IsEmail()
  guestEmail?: string;

  @IsOptional()
  @IsString()
  guestName?: string;

  @IsOptional()
  @IsString()
  guestSurname?: string;
}

import { IsNotEmpty, IsNumber } from 'class-validator';

export class CreateOfferDto {
  @IsNotEmpty()
  @IsNumber()
  price: number;
}

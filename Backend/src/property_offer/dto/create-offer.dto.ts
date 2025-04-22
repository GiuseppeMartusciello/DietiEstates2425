import { IsDate, IsEnum, IsNotEmpty, IsNumber, IsUUID } from "class-validator";
import { OfferState } from "../../common/types/offer-state";

export class CreateOfferDto {

  
  @IsNotEmpty()
  @IsNumber()
  price: number;

  @IsNotEmpty()
  @IsUUID()
  propertyId: string;

}

import { IsBoolean, IsEnum, IsNotEmpty } from "class-validator";
import { OfferState } from "src/common/types/offer-state";

export class UpdateOfferDto {

  @IsNotEmpty()
  @IsEnum(OfferState)
  status: OfferState;
}
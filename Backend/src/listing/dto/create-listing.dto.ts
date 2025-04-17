import {
  IsString,
  IsNotEmpty,
  IsArray,
  IsBoolean,
  IsEnum,
  IsNumber,
  IsUUID,
  IsOptional,
  IsInt,
} from 'class-validator';
import { ListingCategory } from 'src/common/types/listing-category';

export class CreateListingDto {
  @IsString()
  @IsNotEmpty()
  address: string;

  @IsString()
  @IsNotEmpty()
  title: string;

  @IsString()
  @IsNotEmpty()
  municipality: string;

  @IsString()
  @IsNotEmpty()
  city: string;

  @IsString()
  @IsNotEmpty()
  postalCode: string;

  @IsString()
  @IsNotEmpty()
  province: string;

  @IsString()
  @IsNotEmpty()
  size: string;

  @IsInt()
  numberOfRooms: number;

  @IsString()
  @IsNotEmpty()
  energyClass: string;

  @IsString()
  @IsOptional()
  position: string;

  @IsArray()
  @IsOptional()
  @IsString({ each: true })
  nearbyPlaces: string[];

  @IsString()
  @IsNotEmpty()
  description: string;

  @IsNumber()
  price: number;

  @IsEnum(ListingCategory)
  category: ListingCategory;

  @IsString()
  floor: string;

  @IsBoolean()
  hasElevator: boolean;

  @IsBoolean()
  hasAirConditioning: boolean;

  @IsBoolean()
  hasGarage: boolean;

  @IsOptional()
  @IsUUID()
  agentId: string;
}

import {
  IsString,
  IsBoolean,
  IsOptional,
  IsInt,
  IsEnum,
  Validate,
} from 'class-validator';
import { ListingCategory } from 'src/common/types/listing-category';
import { SearchType } from 'src/common/types/searchType.enum';
import { MunicipalityCoordinatesValidator } from 'src/common/validator/municipality-coordinates.validator';

export class ResearchListingDto {

  @IsEnum(SearchType)
  searchType: SearchType;

  @IsOptional()
  @IsString()
  municipality: string;

  @IsOptional()
  latitude?: number;

  @IsOptional()
  longitude?: number;

  @IsOptional()
  @IsInt()
  radius?: number; 

  @IsOptional()
  @IsInt()
  minPrice?: number;

  @IsOptional()
  @IsInt()
  maxPrice?: number;

  @IsOptional()
  @IsInt()
  numberOfRooms?: number;

  @IsOptional()
  @IsEnum(ListingCategory)
  category?: ListingCategory;

  @IsOptional()
  @IsInt()
  minSize?: number; //dimensione in metri quadri

  @IsOptional()
  @IsString()
  energyClass?: string;

  @IsOptional()
  @IsBoolean()
  hasElevator?: boolean;

  @IsOptional()
  @IsBoolean()
  hasAirConditioning?: boolean;

  @IsOptional()
  @IsBoolean()
  hasGarage?: boolean;

  //@Validate(MunicipalityCoordinatesValidator)
  //dummyPropertyForCustomValidation: any; // workaround per attivare la validazione sulla classe
}

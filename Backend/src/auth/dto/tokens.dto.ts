import { IsBoolean, IsNotEmpty, IsString } from 'class-validator';

export class TokensDto {
  @IsNotEmpty()
  @IsString()
  accessToken: string;

  @IsBoolean()
  mustChangePassword: boolean;
}

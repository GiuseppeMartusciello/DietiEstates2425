import { IsNotEmpty, IsStrongPassword } from 'class-validator';

export class CredentialDto {
  @IsNotEmpty()
  @IsStrongPassword()
  currentPassword: string;

  @IsNotEmpty()
  @IsStrongPassword()
  newPassword: string;
}

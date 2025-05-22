import { IsNotEmpty, IsStrongPassword } from 'class-validator';

export class CredentialDto {
  @IsNotEmpty()
  currentPassword: string;

  @IsNotEmpty()
  @IsStrongPassword()
  newPassword: string;
}

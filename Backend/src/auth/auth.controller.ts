import { Body, Controller, Get, Post, UseGuards } from '@nestjs/common';
import { AuthService } from './auth.service';
import { AuthCredentialDto } from './dto/auth.credentials.dto';
import { SignInDto } from './dto/signin.credentials.dto';

@Controller('auth')
export class AuthController {
  constructor(private readonly authService: AuthService) {}

  @Post('/signup')
  signup(@Body() authCredentialDto: AuthCredentialDto) {
    return this.authService.signUp(authCredentialDto);
  }

  @Post('/signin')
  signin(@Body() credentials: SignInDto) {
    return this.authService.signIn(credentials);
  }

  @Post('/google-token')
  async googleTokenLogin(@Body() body: { idToken: string }) {
    console.log("Google token e' stato chiamato");
    return this.authService.verifyGoogleTokenAndLogin(body.idToken);
  }
}

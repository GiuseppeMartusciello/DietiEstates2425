import { Body, Controller, Get, Post, Req } from '@nestjs/common';
import { AuthService } from './auth.service';
import { TokensDto } from './dto/tokens.dto';
import { AuthCredentialDto } from './dto/auth.credentials.dto';
import { SignInDto } from './dto/signin.credentials.dto';

@Controller('auth')
export class AuthController {
  constructor(private readonly authService: AuthService) {}

  @Post('/signup')
  signup(@Body() authCredentialDto: AuthCredentialDto, @Req() req: Request) {
    return this.authService.signUp(authCredentialDto);
  }

  @Post('/signin')
  signin(@Body() credentials: SignInDto) {
    return this.authService.signIn(credentials);
  }
}

import { Body, Controller, Get, Post, Req, UseGuards } from '@nestjs/common';
import { AuthService } from './auth.service';
import { AuthCredentialDto } from './dto/auth.credentials.dto';
import { SignInDto } from './dto/signin.credentials.dto';
import { AuthGuard } from '@nestjs/passport';
import { GetUser } from './get-user.decorator';

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

  @Get('google')
  @UseGuards(AuthGuard('google'))
  async googleLogin() {}

  @Get('google/redirect')
  @UseGuards(AuthGuard('google'))
  async googleRedirect(@GetUser() user) {
    return this.authService.socialLogin(user);
  }
}

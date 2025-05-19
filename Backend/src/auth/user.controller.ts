import { Body, Controller, Patch, UseGuards } from '@nestjs/common';
import { UserService } from './user.service';
import { AuthGuard } from '@nestjs/passport';
import { CredentialDto } from 'src/agency-manager/dto/credentials.dto';
import { GetUser } from './get-user.decorator';
import { UserItem } from 'src/common/types/userItem';

@Controller('user')
@UseGuards(AuthGuard('jwt'))
export class UserController {
  constructor(private readonly userService: UserService) {}

  @Patch('/change-password')
  changePassword(
    @Body() credentials: CredentialDto,
    @GetUser() user: UserItem,
  ) {
    return this.userService.changePassword(credentials, user.id);
  }
}

import {
  Body,
  Controller,
  Patch,
  Post,
  UnauthorizedException,
  UseGuards,
} from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';
import { RolesGuard } from 'src/common/guards/roles.guard';
import { AgencyManagerService } from './agency-manager.service';
import { AuthCredentialDto } from 'src/auth/dto/auth.credentials.dto';
import { SignInDto } from 'src/auth/dto/signin.credentials.dto';
import { CredentialDto } from './dto/credentials.dto';
import { GetUser } from 'src/auth/get-user.decorator';
import { UserItem } from 'src/common/types/userItem';
import { Roles } from 'src/common/decorator/roles.decorator';
import { UserRoles } from 'src/common/types/user-roles';
import { CreateSupportAdminDto } from './dto/create-support-admin.dto';

@Controller('agency-manager')
@UseGuards(AuthGuard('jwt'), RolesGuard)
export class AgencyManagerController {
  constructor(private readonly managerService: AgencyManagerService) {}

  @Patch('/change-credentials')
  @Roles(UserRoles.MANAGER)
  changePassword(
    @Body() credentials: CredentialDto,
    @GetUser() user: UserItem,
  ) {
    if (!user.isDeafaultPassword) throw new UnauthorizedException();

    return this.managerService.changePassword(credentials, user.id);
  }

  @Post('support-admin')
  @Roles(UserRoles.MANAGER)
  createSupportAdmin(
    @Body() createSupportAdminDto: CreateSupportAdminDto,
    @GetUser() user: UserItem,
  ) {
    return this.managerService.createSupportAdmin(createSupportAdminDto, user);
  }
}

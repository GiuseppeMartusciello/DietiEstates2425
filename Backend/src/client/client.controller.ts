import {
  Controller,
  Get,
  Param,
  ParseUUIDPipe,
  UseGuards,
} from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';
import { ClientService } from './client.service';
import { Client } from './client.entity';
import { RolesGuard } from 'src/common/guards/roles.guard';
import { Roles } from 'src/common/decorator/roles.decorator';
import { UserRoles } from 'src/common/types/user-roles';
import { GetUser } from 'src/auth/get-user.decorator';
import { UserItem } from 'src/common/types/userItem';

@Controller('client')
@UseGuards(AuthGuard('jwt'), RolesGuard)
export class ClientController {
  constructor(private clientService: ClientService) {}

  @Get('/me')
  @Roles(UserRoles.CLIENT)
  getMyClient(@GetUser() client: UserItem): UserItem {
    return client;
  }

  //   @Get('/:id')
  //   @Roles(UserRoles.MANAGER, UserRoles.SUPPORT_ADMIN)
  //   getClientById(@Param('id', new ParseUUIDPipe()) id: string): Promise<Client> {
  //     return this.clientService.getClientById(id);
  //   }
}

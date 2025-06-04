import { Body, Controller, Get, Patch, UseGuards } from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';
import { ClientService } from './client.service';
import { RolesGuard } from 'src/common/guards/roles.guard';
import { Roles } from 'src/common/decorator/roles.decorator';
import { UserRoles } from 'src/common/types/user-roles';
import { GetUser } from 'src/auth/get-user.decorator';
import { UserItem } from 'src/common/types/userItem';
import { UpdateNotificationPreferenceDto } from './Dto/updateNotificationPreference.dto';

@Controller('client')
@UseGuards(AuthGuard('jwt'), RolesGuard)
export class ClientController {
  constructor(private readonly clientService: ClientService) {}

  @Get('/me')
  @Roles(UserRoles.CLIENT)
  getMyClient(@GetUser() user: UserItem) {
    return {
      id: user.id,
      name: user.name,
      surname: user.surname,
      email: user.email,
      birthDate: user.birthDate,
      gender: user.gender,
      phone: user.phone,
      address: user.client?.address,
      promotionalNotification: user.client?.promotionalNotification,
      offerNotification: user.client?.offerNotification,
      searchNotification: user.client?.searchNotification,
    };
  }

  @Patch('/notification-preference')
  @Roles(UserRoles.CLIENT)
  async updateNotificationPreference(
    @GetUser() user: UserItem,
    @Body() dto: UpdateNotificationPreferenceDto,
  ): Promise<{ message: string }> {
    await this.clientService.updateNotificationPreference(user.id, dto);
    return { message: 'Preferenza notifiche aggiornata con successo' };
  }
}

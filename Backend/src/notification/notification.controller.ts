import {
  Body,
  Controller,
  Get,
  Param,
  Patch,
  Post,
  UseGuards,
} from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';
import { RolesGuard } from 'src/common/guards/roles.guard';
import { NotificationService } from './notification.service';
import { UserRoles } from 'src/common/types/user-roles';
import { Roles } from 'src/common/decorator/roles.decorator';
import { GetUser } from 'src/auth/get-user.decorator';
import { UserItem } from 'src/common/types/userItem';
import { Notification } from './notification.entity';
import { CreateNotificationDto } from './dto/create-notification.dto';
import { PushNotificationService } from './push-notifications/push-notification.service';

@Controller('notification')
@UseGuards(AuthGuard('jwt'), RolesGuard)
export class NotificationController {
  constructor(
    private readonly notificationService: NotificationService,
    private readonly pushNotificationService: PushNotificationService,
  ) {}

  @Post('/listing/:listingId')
  //@Roles(UserRoles.ADMIN, UserRoles.SUPPORT_ADMIN)
  createPromotionalNotification(
    @GetUser() user: UserItem,
    @Body() createNotificationDto: CreateNotificationDto,
    @Param('listingId') listingId: string,
  ): Promise<Notification> {
    return this.notificationService.createPromotionalNotification(
      user,
      createNotificationDto,
      listingId,
    );
  }

  // lista di tutte le notifiche non lette
  @Get('/Notifications')
  getNotification(@GetUser() user: UserItem): Promise<Notification[]> {
    return this.notificationService.getNotifications(user.id);
  }

  //utente clicca su una notifica
  //l 'id deve essere quello di notifica e non di userNotification
  //è possibile farlo perche in getNotification vengono restituiti gli id di notification
  //si puo e si deve ottenere da lì l id di notification
  @Get('/:notificationId')
  getNotificationById(
    @Param('notificationsId') notificationId: string,
  ): Promise<Notification> {
    return this.notificationService.getNotificationById(notificationId);
  }

  @Patch('/:notificationId')
  updateNotification(
    @GetUser() user: UserItem,
    @Param('notificationId') userNotificationId: string,
  ): Promise<void> {
    return this.notificationService.updateNotification(
      user,
      userNotificationId,
    );
  }

  //non testato non so se funziona
  @Post('test-push')
  @Roles(UserRoles.ADMIN, UserRoles.SUPPORT_ADMIN, UserRoles.CLIENT)
  async testPush(@Body() body: { token: string }) {
    return this.pushNotificationService.sentToDevice(
      body.token,
      'Test FCM da NestJS',
      'Questa è una notifica inviata dal backend',
    );
  }
}

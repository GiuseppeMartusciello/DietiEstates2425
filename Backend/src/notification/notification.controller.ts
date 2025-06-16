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

@Controller('notification')
@UseGuards(AuthGuard('jwt'), RolesGuard)
export class NotificationController {
  constructor(
    private readonly notificationService: NotificationService,
  ) {}

  @Post('/listing/:listingId')
  @Roles(UserRoles.ADMIN)
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
  Notifications(@GetUser() user: UserItem): Promise<Notification[]> {
    return this.notificationService.Notifications(user.id);
  }

  //utente clicca su una notifica
  //l 'id deve essere quello di notifica e non di userNotification
  //è possibile farlo perche in getNotification vengono restituiti gli id di notification
  //si puo e si deve ottenere da lì l id di notification
  @Get('/:notificationId')
  NotificationById(
    @Param('notificationId') notificationId: string,
  ): Promise<Notification> {
    return this.notificationService.NotificationById(notificationId);
  }

  @Patch('/:notificationId')
  updateNotification(
    @Param('notificationId') userNotificationId: string,
  ): Promise<void> {
    return this.notificationService.Notification(
      userNotificationId,
    );
  }
}

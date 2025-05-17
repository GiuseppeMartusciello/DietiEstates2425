import { IsEnum, IsNotEmpty, IsString } from "class-validator";
import { NotificationType } from "src/common/types/notification.enum";

export class CreateNotificationDto {

    @IsNotEmpty()
    @IsEnum(NotificationType)
    category: NotificationType;

    @IsNotEmpty()
    @IsString()
    title: string;
    

    @IsNotEmpty()
    @IsString()
    description: string;

 }
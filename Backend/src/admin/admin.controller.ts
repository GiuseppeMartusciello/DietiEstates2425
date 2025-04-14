import {
  Body,
  Controller,
  Delete,
  Param,
  Post,
  Req,
  UseGuards,
} from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';
import { Agency } from 'src/agency/agency.entity';
import { RolesGuard } from 'src/common/guards/roles.guard';
import { Repository } from 'typeorm';
import { AdminService } from './admin.service';
import { Roles } from 'src/common/decorator/roles.decorator';
import { UserRoles } from 'src/common/types/user-roles';
import { GetUser } from 'src/auth/get-user.decorator';
import { UserItem } from 'src/common/types/userItem';
import { CreateAgencyDto } from './dto/create-agency.dto';

@Controller('admin')
@UseGuards(AuthGuard('jwt'), RolesGuard)
export class AdminController {
  constructor(private readonly adminService: AdminService) {}

  @Post('agency')
  @Roles(UserRoles.ADMIN)
  createAgency(@Body() createAgencyDto: CreateAgencyDto, @GetUser() user) {
    console.log(user);
    return this.adminService.createAgency(createAgencyDto);
  }

  @Delete('agency/:id/delete')
  @Roles(UserRoles.ADMIN)
  removeAgencyById(@Param('id') id: string) {
    return this.adminService.removeAgencyById(id);
  }
}

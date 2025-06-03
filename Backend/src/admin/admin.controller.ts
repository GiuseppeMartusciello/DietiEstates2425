import {
  Body,
  Controller,
  Delete,
  Param,
  Post,
  UseGuards,
} from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';
import { RolesGuard } from 'src/common/guards/roles.guard';
import { AdminService } from './admin.service';
import { Roles } from 'src/common/decorator/roles.decorator';
import { UserRoles } from 'src/common/types/user-roles';
import { CreateAgencyDto } from './dto/create-agency.dto';

@Controller('admin')
@UseGuards(AuthGuard('jwt'), RolesGuard)
export class AdminController {
  constructor(private readonly adminService: AdminService) {}

  @Post('agency')
  @Roles(UserRoles.ADMIN)
  createAgency(@Body() createAgencyDto: CreateAgencyDto) {
    return this.adminService.createAgency(createAgencyDto);
  }

  @Delete('agency/:id')
  @Roles(UserRoles.ADMIN)
  removeAgencyById(@Param('id') id: string) {
    return this.adminService.removeAgencyById(id);
  }
}

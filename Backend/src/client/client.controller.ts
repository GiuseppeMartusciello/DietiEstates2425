import { Controller, Get, Param, UseGuards } from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';
import { ClientService } from './client.service';
import { Client } from './client.entity';

@Controller('client')
@UseGuards(AuthGuard('jwt'))
export class ClientController {
    constructor(private clientService: ClientService){}

    @Get('/:id')
    getClientById(@Param('id') id: string): Promise<Client>{
        return this.clientService.getClientById(id);
    }
}

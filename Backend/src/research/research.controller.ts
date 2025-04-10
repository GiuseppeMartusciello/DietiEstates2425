import { Body, Controller, Get, Param, Post, UseGuards } from '@nestjs/common';
import { ResearchService } from './research.service';
import { Research } from './research.entity';
import { CreateResearchDto } from './create-research.dto';
import { AuthGuard } from '@nestjs/passport';
import { GetUser } from 'src/auth/get-user.decorator';
import { User } from 'src/auth/user.entity';
import { ClientService } from 'src/client/client.service';

@Controller('research')
@UseGuards(AuthGuard())
export class ResearchController {
    constructor(
        private researchService: ResearchService,
        private clientService: ClientService
    ){}

    @Get('/:id')
    getResearchById(@Param('id') id: string): Promise<Research>{
        //const client = await this.clientService.getClientById(user.id);
        return this.researchService.getResearchById(id);
    }

    @Get('client/:clientId')
    getResearchByClientId(@Param('clientId') id: string): Promise<Research>{
        return this.researchService.getResearchByClientId(id);
    }


    @Post()
    async createResearch(
        @Body() createResearchDto: CreateResearchDto,
        @GetUser() user: User,
    ): Promise<Research> {
        const client = await this.clientService.getClientById(user.id);
        return this.researchService.createResearch(createResearchDto,client);
    }
    
}

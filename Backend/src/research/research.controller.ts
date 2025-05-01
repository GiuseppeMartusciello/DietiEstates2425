import { Body, Controller, Delete, Get, Param, Patch, Post, UnauthorizedException, UseGuards } from '@nestjs/common';
import { ResearchService } from './research.service';
import { Research } from './research.entity';
import { CreateResearchDto } from './dto/create-research.dto';
import { AuthGuard } from '@nestjs/passport';
import { GetUser } from 'src/auth/get-user.decorator';
import { User } from 'src/auth/user.entity';
import { ClientService } from 'src/client/client.service';
import { UserItem } from 'src/common/types/userItem';
import { UserRoles } from 'src/common/types/user-roles';
import { Roles } from 'src/common/decorator/roles.decorator';
import { RolesGuard } from 'src/common/guards/roles.guard';
import { repeat } from 'rxjs';


@Controller('research')
@UseGuards(AuthGuard('jwt'),RolesGuard)
export class ResearchController {
    constructor(
        private researchService: ResearchService,
    ){}

    @Get('my')
    @Roles(UserRoles.CLIENT)
    getResearchByClientId(@GetUser() user: UserItem): Promise<Research[]>{
        return this.researchService.getResearchByClientId(user.id);
    }

    @Delete('/:id')
    @Roles(UserRoles.CLIENT)
    deleteResearch(@Param('id') id: string, @GetUser() user: UserItem): Promise<void>{
        const client = user.client;
        if(!client)
            throw new UnauthorizedException();

        return this.researchService.deleteResearch(id,client);
    }

    @Post()
    @Roles(UserRoles.CLIENT)
    createResearch(
        @Body() createResearchDto: CreateResearchDto,
        @GetUser() user: UserItem,
    ): Promise<Research> {
        const client = user.client;
        if(!client)
            throw new UnauthorizedException();

        return this.researchService.createResearch(createResearchDto,client);
    }

    @Get('last10')
    @Roles(UserRoles.CLIENT)
    getLast10ResearchByClientId(@GetUser() user: UserItem): Promise<Research[]> {
        const client = user.client;
        if(!client)
            throw new UnauthorizedException();

        return this.researchService.getLast10ResearchByClientId(user.id);
    }

    @Patch('/:id')
    @Roles(UserRoles.CLIENT)
    updateResearch(
        @GetUser() user: UserItem,
        @Param('id') researchId: string,
    ): Promise<Research> {
        const client = user.client;
        if(!client)
            throw new UnauthorizedException();

        return this.researchService.updateResearch(researchId,client);
    }
    
}

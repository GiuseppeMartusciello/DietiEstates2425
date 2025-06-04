import {
  Body,
  Controller,
  Delete,
  Get,
  Param,
  Patch,
  Post,
  UnauthorizedException,
  UseGuards,
} from '@nestjs/common';
import { ResearchService } from './research.service';
import { Research } from './research.entity';
import { ResearchListingDto } from './dto/create-research.dto';
import { AuthGuard } from '@nestjs/passport';
import { GetUser } from 'src/auth/get-user.decorator';
import { UserItem } from 'src/common/types/userItem';
import { UserRoles } from 'src/common/types/user-roles';
import { Roles } from 'src/common/decorator/roles.decorator';
import { RolesGuard } from 'src/common/guards/roles.guard';
import { Listing } from 'src/listing/Listing.entity';


@Controller('research')
@UseGuards(AuthGuard('jwt'),RolesGuard)
export class ResearchController {
    constructor(
        private researchService: ResearchService,
    ){}


    // questo metodo restituisce tutte le ricerche effettuate da un cliente
    @Get()
    @Roles(UserRoles.CLIENT)
    getResearchByClientId(@GetUser() user: UserItem): Promise<Research[]>{
        return this.researchService.getResearchByClientId(user.id);
    }

    //elimina una ricerca
    @Delete('/:id')
    @Roles(UserRoles.CLIENT)
  deleteResearch(
    @Param('id') id: string,
    @GetUser() user: UserItem,
  ): Promise<void> {
        const client = user.client;
        if(!client)
            throw new UnauthorizedException();

        return this.researchService.deleteResearch(id,client);
    }

    //crea una ricerca effettuata da un cliente
    @Post()
    @Roles(UserRoles.CLIENT)
    createResearch(
        @Body() researchListingDto: ResearchListingDto,
        @GetUser() user: UserItem,
    ): Promise<Listing[]> {
        const client = user.client;
        if(!client)
            throw new UnauthorizedException();

        return this.researchService.createResearch(researchListingDto,client);
    }

    //restituisce le ultime 10 ricerche effettuate da un cliente
    @Get('last-TEN')
    @Roles(UserRoles.CLIENT)
    getLast10ResearchByClientId(@GetUser() user: UserItem): Promise<Research[]> {
        const client = user.client;
        if(!client)
            throw new UnauthorizedException();

        return this.researchService.getLast10ResearchByClientId(user.id);
    }


    // aggiorna la ricerca una volta che viene rieffettuata
    @Patch('/:id')
    @Roles(UserRoles.CLIENT)
    updateResearch(
        @GetUser() user: UserItem,
        @Param('id') researchId: string,
    ): Promise<Listing[]> {
        
        const client = user.client;
        if(!client)
            throw new UnauthorizedException();

        return this.researchService.updateResearch(researchId,client);
    }
    
}

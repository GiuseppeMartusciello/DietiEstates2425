import { Controller, Get, Param } from '@nestjs/common';
import { ResearchService } from './research.service';
import { Research } from './research.entity';

@Controller('research')
export class ResearchController {
    constructor(private researchService: ResearchService){}

    @Get('/:id')
    getResearchById(@Param('id') id: string): Promise<Research>{
        return this.researchService.getResearchById(id);
    }
}

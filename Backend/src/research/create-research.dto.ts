import { IsString, isString } from "class-validator";

export class CreateResearchDto{
    @IsString()
    test: string;
}
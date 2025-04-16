import { IsUUID } from "class-validator";

export class Last10ResearchDto {
  @IsUUID()
  id: string;
}
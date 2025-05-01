import { Inject, Injectable } from "@nestjs/common";
import {  Repository } from "typeorm";
import { PropertyOffer } from "./property_offer.entity";
import { Client } from "src/client/client.entity";

@Injectable()
export class OfferRepository extends Repository<PropertyOffer> {
  constructor(@Inject('PropertyOffer') private readonly repository: Repository<PropertyOffer>) {
    super(
      repository.target,
      repository.manager,
      repository.queryRunner
    );
  }

}
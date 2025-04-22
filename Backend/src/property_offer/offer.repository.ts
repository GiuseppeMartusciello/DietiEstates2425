import { Inject, Injectable } from "@nestjs/common";
import { In, Repository } from "typeorm";
import { PropertyOffer } from "./property_offer.entity";

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
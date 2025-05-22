import { Listing } from "../Listing.entity";

export class ListingResponse{
  listing: Listing;
  imageUrls: string[];
}

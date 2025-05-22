export class ListingResponse {
  id: string;
  title: string;
  address: string;
  municipality: string;
  postalCode: string;
  province: string;
  size: string;
  latitude: number;
  longitude: number;
  numberOfRooms: number;
  energyClass: string;
  nearbyPlaces: string[];
  description: string;
  price: number;
  category: string;
  floor: string;
  hasElevator: boolean;
  hasAirConditioning: boolean;
  hasGarage: boolean;
  imageUrls: string[];
}

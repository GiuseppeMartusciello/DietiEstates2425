export class LastOfferDto {
  price: number;
  date: Date;
  state: string;
}

export class ClientWithLastOfferDto {
  userId: string | null;
  name: string;
  surname: string;
  email: string;
  phone: string | null;
  lastOffer: LastOfferDto;
}

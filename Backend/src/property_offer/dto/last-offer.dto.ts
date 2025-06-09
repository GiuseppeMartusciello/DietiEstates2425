export class LastOfferDto {
  id: string;
  price: number;
  date: Date;
  state: string;
  madeByUser: boolean;
}

export class ClientWithLastOfferDto {
  userId: string | null;
  name: string;
  surname: string;
  email: string;
  phone: string | null;
  lastOffer: LastOfferDto;
}

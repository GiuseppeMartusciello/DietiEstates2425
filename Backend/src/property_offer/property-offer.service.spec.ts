import { Test, TestingModule } from '@nestjs/testing';
import { OfferService } from './property-offer.service';
import { Repository } from 'typeorm';
import { PropertyOffer } from './property_offer.entity';
import { getRepositoryToken } from '@nestjs/typeorm';
import { Client } from 'src/client/client.entity';
import { OfferState } from 'src/common/types/offer-state';
import { ListingRepository } from 'src/listing/listing.repository';
import { NotificationService } from 'src/notification/notification.service';
import { ListingService } from 'src/listing/listing.service';

type MockType<T> = {
  [P in keyof T]: jest.Mock<{}>;
};

const repositoryMockFactory: () => Partial<Repository<any>> = jest.fn(() => ({
  find: jest.fn(),
  findOne: jest.fn(),
  create: jest.fn(),
  save: jest.fn(),
  update: jest.fn(),
}));

describe('OfferService - getOffersByListingAndClient', () => {
  let offerService: OfferService;
  let offerRepositoryMock: MockType<Repository<PropertyOffer>>;
  const listingRepositoryMock = {
    find: jest.fn(),
  };
  const notificationServiceMock = {
    createSpecificNotificationOffer: jest.fn(),
  };

  const listingServiceMock = {
    getImagesForListing: jest.fn(), // solo se usato nei metodi
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        OfferService,
        {
          provide: getRepositoryToken(PropertyOffer),
          useFactory: repositoryMockFactory,
        },
        {
          provide: getRepositoryToken(Client),
          useFactory: repositoryMockFactory,
        },
        {
          provide: 'ListingRepository',
          useFactory: repositoryMockFactory,
        },
        {
          provide: ListingService,
          useValue: listingServiceMock,
        },
        {
          provide: ListingRepository,
          useValue: listingRepositoryMock,
        },
        {
          provide: NotificationService,
          useValue: notificationServiceMock,
        },
      ],
    }).compile();

    offerService = module.get<OfferService>(OfferService);
    offerRepositoryMock = module.get(getRepositoryToken(PropertyOffer));
  });

  it('✔️ Dovrebbe ritornare una lista di offerte', async () => {
    const listingId = '1000';
    const userId = '1000';

    const offers: PropertyOffer[] = [
      {
        id: '1',
        price: 300000,
        date: new Date(),
        state: OfferState.PENDING,
        madeByUser: true,
        listing: { id: listingId } as any,
        client: { userId: userId } as any,
      } as PropertyOffer,
    ];

    (
      offerRepositoryMock.find as jest.Mock<Promise<PropertyOffer[]>>
    ).mockResolvedValue(offers);

    const result = await offerService.getOffersByListingAndClient(
      listingId,
      userId,
    );
    expect(result).toEqual(offers);
    expect(offerRepositoryMock.find).toHaveBeenCalledWith({
      where: {
        listing: { id: listingId },
        client: { userId: userId },
      },
      relations: ['client', 'listing'],
      order: { date: 'ASC' },
    });
  });
});

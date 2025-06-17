import { Test, TestingModule } from '@nestjs/testing';
import { OfferService } from './property-offer.service';
import { Repository } from 'typeorm';
import { getRepositoryToken } from '@nestjs/typeorm';
import { PropertyOffer } from './property_offer.entity';
import { ListingRepository } from 'src/listing/listing.repository';
import { NotificationService } from 'src/notification/notification.service';
import { CreateExternalOfferDto } from './dto/create-externalOffer.dto';
import { Agent } from 'src/agent/agent.entity';
import { Agency } from 'src/agency/agency.entity';
import { ListingCategory } from 'src/common/types/listing-category';
import { UserRoles } from 'src/common/types/user-roles';
import { Gender } from 'src/common/types/gender.enum';
import { Provider } from 'src/common/types/provider.enum';

import {
  BadRequestException,
  NotFoundException,
  UnauthorizedException,
} from '@nestjs/common';
import { OfferState } from 'src/common/types/offer-state';
import { Listing } from 'src/listing/Listing.entity';
import { Manager } from 'src/agency-manager/agency-manager.entity';
import { User } from 'src/auth/user.entity';
import { UserItem } from 'src/common/types/userItem';
import { ListingService } from 'src/listing/listing.service';

describe('OfferService - createExternalOffer', () => {
  let service: OfferService;
  let offerRepository: jest.Mocked<Partial<Repository<PropertyOffer>>>;
  let listingRepository: jest.Mocked<Partial<ListingRepository>>;
  let notificationService: jest.Mocked<Partial<NotificationService>>;

  const mockUser: User = {
    id: 'user-1',
    email: 'test@example.com',
    password: 'hashedOldPassword',
    name: '',
    surname: '',
    phone: '',
    birthDate: new Date(),
    role: UserRoles.AGENT,
    gender: Gender.OTHER,
    provider: Provider.LOCAL,
    lastPasswordChangeAt: new Date(),
    createdNotifications: [],
    userNotifications: [],
  };

  const mockAgency: Agency = {
    id: 'agency-1',
    name: 'Test Agency',
    legalAddress: 'Via Roma 1, Roma',
    phone: '1234567890',
    vatNumber: 'IT12345678901',
    listings: [],
    agents: [],
    manager: new Manager(),
    supportAdmins: [],
  };

  const mockAgent: Agent = {
    userId: 'agent-1',
    licenseNumber: 'LIC12345',
    start_date: new Date('2020-01-01'),
    languages: ['it', 'en'],
    user: mockUser,
    agency: mockAgency,
    listings: [],
  };

  const mockWrongAgent: Agent = {
    userId: 'wrong-agent',
    licenseNumber: 'WRONG123',
    start_date: new Date('2022-01-01'),
    languages: ['en'],
    user: mockUser,
    agency: mockAgency,
    listings: [],
  };

  const mockListing: Listing = {
    id: 'listing-1',
    title: 'Beautiful Apartment',
    address: '123 Main St',
    municipality: 'Rome',
    postalCode: '00100',
    province: 'RM',
    size: '100 sqm',
    latitude: 41.9028,
    longitude: 12.4964,
    numberOfRooms: 3,
    energyClass: 'A',
    nearbyPlaces: ['Metro', 'Supermarket'],
    description: 'A great place in Rome.',
    price: 1000,
    category: ListingCategory.SALE, // o un valore valido per l'enum
    floor: '2',
    hasElevator: true,
    hasAirConditioning: true,
    hasGarage: false,
    agency: mockAgency,
    agent: mockAgent,
    propertyOffers: [],
    notifications: [],
  };

  beforeEach(async () => {
    offerRepository = {
      create: jest.fn(),
      save: jest.fn(),
    };

    listingRepository = {
      findOne: jest.fn(),
    };

    notificationService = {
      createSpecificNotificationOffer: jest.fn(),
    };

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        OfferService,
        {
          provide: getRepositoryToken(PropertyOffer),
          useValue: offerRepository,
        },
        { provide: ListingRepository, useValue: listingRepository },
        { provide: NotificationService, useValue: notificationService },
        { provide: ListingService, useValue: {} },
      ],
    }).compile();

    service = module.get<OfferService>(OfferService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('TC1 - should create an external offer successfully', async () => {
    // DTO's definition
    const dto: CreateExternalOfferDto = {
      price: 1000,
      guestEmail: 'guest@example.com',
      guestName: 'Mario',
      guestSurname: 'Rossi',
    };

    const mockOffer = {
      id: 'offer-1',
      ...dto,
      listing: mockListing,
      state: OfferState.PENDING,
      madeByUser: true,
      date: new Date(),
    };

    // MOCK
    (listingRepository.findOne as jest.Mock).mockResolvedValue(mockListing);

    (offerRepository.create as jest.Mock).mockReturnValue(mockOffer);
    (offerRepository.save as jest.Mock).mockResolvedValue(mockOffer);

    // ACT
    const result = await service.createExternalOffer(
      dto,
      mockUser,
      mockListing.id,
    );

    // ASSERT
    expect(result).toEqual(mockOffer);
    expect(offerRepository.create).toHaveBeenCalledWith(
      expect.objectContaining({
        price: dto.price,
        guestEmail: dto.guestEmail,
        guestSurname: dto.guestSurname,
        guestName: dto.guestName,
        madeByUser: true,
      }),
    );
  });

  it('TC2 - should throw NotFoundException if listing does not exist', async () => {
    // DTO's definition
    const dto: CreateExternalOfferDto = {
      price: 1000,
      guestEmail: 'guest@example.com',
      guestName: 'Mario',
      guestSurname: 'Rossi',
    };

    // MOCK
    (listingRepository.findOne as jest.Mock).mockResolvedValue(null);

    // ACT & ASSERT
    await expect(
      service.createExternalOffer(dto, mockUser, 'invalid-listing'),
    ).rejects.toThrow(NotFoundException);
  });

  it('TC3 - should throw UnauthorizedException if user is not authorized', async () => {
    // DTO's definition
    const dto: CreateExternalOfferDto = {
      price: 1000,
      guestEmail: 'guest@example.com',
      guestName: 'Mario',
      guestSurname: 'Rossi',
    };

    // MOCK
    (listingRepository.findOne as jest.Mock).mockResolvedValue(mockListing);

    const unauthorizedUser: UserItem = {
      ...mockUser,
      agent: mockWrongAgent,
    };

    // ACT && ASSERT
    await expect(
      service.createExternalOffer(dto, unauthorizedUser, mockListing.id),
    ).rejects.toThrow(UnauthorizedException);
  });

  it('TC4 - should throw BadRequestException if price is less than or equal to 0', async () => {
    const dto: CreateExternalOfferDto = {
      price: -1,
      guestEmail: 'guest@example.com',
      guestName: 'Mario',
      guestSurname: 'Rossi',
    };

    (listingRepository.findOne as jest.Mock).mockResolvedValue(mockListing);

    await expect(
      service.createExternalOffer(dto, mockUser, mockListing.id),
    ).rejects.toThrow(BadRequestException);
  });

  it('TC5 - should throw BadRequestException if price is greater than listing price', async () => {
    const dto: CreateExternalOfferDto = {
      price: mockListing.price + 1,
      guestEmail: 'guest@example.com',
      guestName: 'Mario',
      guestSurname: 'Rossi',
    };

    (listingRepository.findOne as jest.Mock).mockResolvedValue(mockListing);

    await expect(
      service.createExternalOffer(dto, mockUser, mockListing.id),
    ).rejects.toThrow(BadRequestException);
  });
});

describe('OfferService - getLatestOffersByListingId', () => {
  let service: OfferService;
  let offerRepository: jest.Mocked<Partial<Repository<PropertyOffer>>>;
  let listingRepository: jest.Mocked<Partial<ListingRepository>>;
  let notificationService: jest.Mocked<Partial<NotificationService>>;
  let listingService: jest.Mocked<Partial<ListingService>>;

  const mockUser: UserItem = {
    id: 'user-1',
    email: 'test@example.com',
    password: 'hashedOldPassword',
    name: '',
    surname: '',
    phone: '',
    birthDate: new Date(),
    role: UserRoles.AGENT,
    gender: Gender.OTHER,
    provider: Provider.LOCAL,
    lastPasswordChangeAt: new Date(),
    createdNotifications: [],
    userNotifications: [],
  };

  const mockListing: Listing = {
    id: 'listing-1',
    title: 'Appartamento centro',
    address: 'Via Roma',
    municipality: 'Roma',
    postalCode: '00100',
    province: 'RM',
    size: '90 mq',
    latitude: 0,
    longitude: 0,
    numberOfRooms: 3,
    energyClass: 'A',
    nearbyPlaces: [],
    description: '',
    price: 100000,
    category: ListingCategory.SALE,
    floor: '2',
    hasElevator: true,
    hasAirConditioning: true,
    hasGarage: false,
    agency: { id: 'agency-1' } as Agency,
    agent: { userId: 'user-1' } as Agent,
    propertyOffers: [],
    notifications: [],
  };

  beforeEach(async () => {
    offerRepository = {
      createQueryBuilder: jest.fn(),
    };

    listingRepository = {
      findOne: jest.fn(),
    };

    listingService = {};

    notificationService = {
      createSpecificNotificationOffer: jest.fn(),
      createPromotionalNotification: jest.fn(),
      Notifications: jest.fn(),
      NotificationById: jest.fn(),
      Notification: jest.fn(),
    } as jest.Mocked<Partial<NotificationService>>;

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        OfferService,
        {
          provide: getRepositoryToken(PropertyOffer),
          useValue: offerRepository,
        },
        {
          provide: ListingRepository,
          useValue: listingRepository,
        },

        { provide: NotificationService, useValue: notificationService },
        {
          provide: ListingService,
          useValue: listingService,
        },
      ],
    }).compile();

    service = module.get<OfferService>(OfferService);
    jest
      .spyOn(service as any, 'checkAuthorization')
      .mockImplementation(() => {});
  });

  afterEach(() => jest.clearAllMocks());

  it('TC1 - should return latest offer for each client on success', async () => {
    // MOCK
    (listingRepository.findOne as jest.Mock).mockResolvedValue(mockListing);

    const mockQueryBuilder = {
      distinctOn: jest.fn().mockReturnThis(),
      innerJoinAndSelect: jest.fn().mockReturnThis(),
      leftJoinAndSelect: jest.fn().mockReturnThis(),
      where: jest.fn().mockReturnThis(),
      andWhere: jest.fn().mockReturnThis(),
      orderBy: jest.fn().mockReturnThis(),
      addOrderBy: jest.fn().mockReturnThis(),
      getMany: jest.fn().mockResolvedValue([
        {
          id: 'offer-1',
          price: 1000,
          date: new Date('2023-01-01'),
          state: OfferState.PENDING,
          madeByUser: true,
          client: {
            userId: 'user-client-1',
            user: {
              name: 'Mario',
              surname: 'Rossi',
              email: 'mario@example.com',
              phone: '123456789',
            },
          },
        },
      ]),
    };

    (offerRepository.createQueryBuilder as jest.Mock).mockReturnValue(
      mockQueryBuilder,
    );

    // ACT
    const result = await service.getLatestOffersByListingId(
      mockListing.id,
      mockUser,
    );

    // ASSERT
    expect(result).toEqual([
      {
        userId: 'user-client-1',
        name: 'Mario',
        surname: 'Rossi',
        email: 'mario@example.com',
        phone: '123456789',
        lastOffer: {
          id: 'offer-1',
          price: 1000,
          date: new Date('2023-01-01'),
          state: OfferState.PENDING,
          madeByUser: true,
        },
      },
    ]);
  });

  it('TC2 - should throw UnauthorizedException if listing not found', async () => {
    // MOCK
    (listingRepository.findOne as jest.Mock).mockResolvedValue(null);

    // ACT && ASSERT
    await expect(
      service.getLatestOffersByListingId('invalid-id', mockUser),
    ).rejects.toThrow(UnauthorizedException);
  });

  it('TC3 - should throw UnauthorizedException if user not authorized', async () => {
    // MOCK
    (listingRepository.findOne as jest.Mock).mockResolvedValue(mockListing);

    jest.spyOn(service as any, 'checkAuthorization').mockImplementation(() => {
      throw new UnauthorizedException();
    });

    // ACT && ASSERT
    await expect(
      service.getLatestOffersByListingId(mockListing.id, mockUser),
    ).rejects.toThrow(UnauthorizedException);
  });
});

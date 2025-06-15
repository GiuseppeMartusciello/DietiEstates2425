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
    const dto: CreateExternalOfferDto = {
      price: 1000,
      guestEmail: 'guest@example.com',
      guestName: 'Mario',
      guestSurname: 'Rossi',
    };

    (listingRepository.findOne as jest.Mock).mockResolvedValue(mockListing);

    jest
      .spyOn(service as any, 'checkAuthorization')
      .mockImplementation(() => {});

    const mockOffer = {
      id: 'offer-1',
      ...dto,
      listing: mockListing,
      state: OfferState.PENDING,
      madeByUser: true,
      date: new Date(),
    };

    (offerRepository.create as jest.Mock).mockReturnValue(mockOffer);
    (offerRepository.save as jest.Mock).mockResolvedValue(mockOffer);

    const result = await service.createExternalOffer(
      dto,
      mockUser,
      mockListing.id,
    );

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

  it('TC2 - should throw BadRequestException if guest fields are all missing', async () => {
    const dto: CreateExternalOfferDto = {
      price: 1000,
      guestEmail: '',
      guestName: '',
      guestSurname: '',
    };

    await expect(
      service.createExternalOffer(dto, mockUser, 'listing-1'),
    ).rejects.toThrow(BadRequestException);
  });

  it('TC3 - should throw NotFoundException if listing does not exist', async () => {
    const dto: CreateExternalOfferDto = {
      price: 1000,
      guestEmail: 'guest@example.com',
      guestName: 'Mario',
      guestSurname: 'Rossi',
    };
    (listingRepository.findOne as jest.Mock).mockResolvedValue(null);

    await expect(
      service.createExternalOffer(dto, mockUser, 'invalid-listing'),
    ).rejects.toThrow(NotFoundException);
  });

  it('TC4 - should throw UnauthorizedException if user is not authorized', async () => {
    const dto: CreateExternalOfferDto = {
      price: 1000,
      guestEmail: 'guest@example.com',
      guestName: 'Mario',
      guestSurname: 'Rossi',
    };

    (listingRepository.findOne as jest.Mock).mockResolvedValue(mockListing);
    jest.spyOn(service as any, 'checkAuthorization').mockImplementation(() => {
      throw new UnauthorizedException();
    });

    const unauthorizedUser: UserItem = {
      ...mockUser,
      agent: mockWrongAgent,
    };

    await expect(
      service.createExternalOffer(dto, unauthorizedUser, mockListing.id),
    ).rejects.toThrow(UnauthorizedException);
  });
});

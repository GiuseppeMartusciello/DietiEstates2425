import { Test, TestingModule } from '@nestjs/testing';
import { UserService } from './user.service';
import { getRepositoryToken } from '@nestjs/typeorm';
import { User } from './user.entity';
import { Repository } from 'typeorm';
import { CredentialDto } from 'src/agency-manager/dto/credentials.dto';
import * as bcrypt from 'bcrypt';
import { NotFoundException, UnauthorizedException } from '@nestjs/common';
import { UserRoles } from 'src/common/types/user-roles';
import { Gender } from 'src/common/types/gender.enum';
import { Provider } from 'src/common/types/provider.enum';

describe('UserService - changePassword', () => {
  let userService: UserService;
  let userRepository: jest.Mocked<Partial<Repository<User>>>;

  const mockUser: User = {
    id: 'user-1',
    email: 'test@example.com',
    password: 'hashedOldPassword',
    name: '',
    surname: '',
    phone: '',
    birthDate: new Date(),
    role: UserRoles.CLIENT,
    gender: Gender.OTHER,
    provider: Provider.LOCAL,
    lastPasswordChangeAt: new Date(),
    createdNotifications: [],
    userNotifications: [],
  };

  beforeEach(async () => {
    userRepository = {
      findOne: jest.fn(),
      save: jest.fn(),
    };

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        UserService,
        {
          provide: getRepositoryToken(User),
          useValue: userRepository,
        },
      ],
    }).compile();

    userService = module.get<UserService>(UserService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('✔️ Dovrebbe aggiornare la password se le credenziali sono corrette', async () => {
    const dto: CredentialDto = {
      currentPassword: 'oldPass123',
      newPassword: 'newPass456!',
    };

    const mockUserCopy = JSON.parse(JSON.stringify(mockUser));
    (userRepository.findOne as jest.Mock).mockResolvedValue(mockUserCopy);

    jest.spyOn(bcrypt, 'compare').mockResolvedValue(true);
    jest.spyOn(bcrypt, 'genSalt').mockResolvedValue('salt');
    jest.spyOn(bcrypt, 'hash').mockResolvedValue('newHashedPassword');

    const result = await userService.changePassword(dto, mockUser.id);

    expect(userRepository.findOne).toHaveBeenCalledWith({
      where: { id: mockUser.id },
    });

    expect(bcrypt.compare).toHaveBeenCalledWith(
      dto.currentPassword,
      mockUser.password,
    );
    expect(userRepository.save).toHaveBeenCalledWith(
      expect.objectContaining({
        password: 'newHashedPassword',
      }),
    );
    expect(result).toEqual({ message: 'Password updated successfully' });
  });

  it('❌ Dovrebbe lanciare NotFoundException se l’utente non esiste', async () => {
    (userRepository.findOne as jest.Mock).mockResolvedValue(null);
    const dto: CredentialDto = {
      currentPassword: 'any',
      newPassword: 'any',
    };

    await expect(userService.changePassword(dto, 'invalid-id')).rejects.toThrow(
      NotFoundException,
    );
  });

  it('❌ Dovrebbe lanciare UnauthorizedException se la password è errata', async () => {
    (userRepository.findOne as jest.Mock).mockResolvedValue(mockUser);
    jest.spyOn(bcrypt, 'compare').mockResolvedValue(false);

    const dto: CredentialDto = {
      currentPassword: 'wrongPass',
      newPassword: 'newPass',
    };

    await expect(userService.changePassword(dto, mockUser.id)).rejects.toThrow(
      UnauthorizedException,
    );
  });
});

/*
  Strategia di test adottata:
  Abbiamo identificato le classi di equivalenza per i parametri del metodo changePassword, considerando le condizioni di validità dei dati di input:

  userId: esistente (V), inesistente (NV)

  currentPassword: corretto (V), errato (NV)

  Usando la strategia N-WEct, abbiamo definito il numero minimo di test (3) per coprire tutte le combinazioni delle classi di equivalenza valide e non valide, ottenendo:

  Aggiornamento corretto della password

  Mancato utente → NotFoundException

  Password errata → UnauthorizedExceptio

*/

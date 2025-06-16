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

// Tests defined using N-WECT: each equivalence class is covered at least once.
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

  it('TC1 - should update password if currentPassword matches and user exists', async () => {
    // DTO's definition
    const dto: CredentialDto = {
      currentPassword: 'oldPass123',
      newPassword: 'newPass456!',
    };

    const newHashedPassword = 'newHashedPassword123';

    const mockUserCopy = JSON.parse(JSON.stringify(mockUser));

    // MOCK
    (userRepository.findOne as jest.Mock).mockResolvedValue(mockUserCopy);
    (userRepository.save as jest.Mock).mockResolvedValue(mockUserCopy);

    jest.spyOn(bcrypt, 'compare').mockResolvedValue(true);
    jest
      .spyOn(userService as any, 'hashPassword')
      .mockResolvedValue(newHashedPassword);

    // ACT
    const result = await userService.changePassword(dto, mockUser.id);

    // ASSERT
    expect(userRepository.findOne).toHaveBeenCalledWith({
      where: { id: mockUser.id },
    });

    expect(bcrypt.compare).toHaveBeenCalledWith(
      dto.currentPassword,
      mockUser.password,
    );
    expect(userRepository.save).toHaveBeenCalledWith(
      expect.objectContaining({
        password: newHashedPassword,
      }),
    );

    expect(result).toEqual({ message: 'Password updated successfully' });
  });

  it('TC2 - should throw NotFoundException if user is not found', async () => {
    // DTO's definition
    const dto: CredentialDto = {
      currentPassword: 'any',
      newPassword: 'any',
    };

    // MOCK
    (userRepository.findOne as jest.Mock).mockResolvedValue(null);

    // ACT & ASSERT
    await expect(userService.changePassword(dto, 'invalid-id')).rejects.toThrow(
      NotFoundException,
    );
  });

  it('TC3 - should throw UnauthorizedException if currentPassword is wrong', async () => {
    // DTO's definition
    const dto: CredentialDto = {
      currentPassword: 'wrongPass',
      newPassword: 'newPass',
    };

    // MOCK
    (userRepository.findOne as jest.Mock).mockResolvedValue(mockUser);
    jest.spyOn(bcrypt, 'compare').mockResolvedValue(false);

    // ACT & ASSERT
    await expect(userService.changePassword(dto, mockUser.id)).rejects.toThrow(
      UnauthorizedException,
    );
  });
});

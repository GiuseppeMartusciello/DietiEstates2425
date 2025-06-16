import { Test, TestingModule } from '@nestjs/testing';
import { AgencyService } from './agency.service';
import { getRepositoryToken } from '@nestjs/typeorm';
import { User } from 'src/auth/user.entity';
import { Agent } from 'src/agent/agent.entity';
import { Agency } from 'src/agency/agency.entity';
import { Repository } from 'typeorm';
import { NotFoundException, UnauthorizedException } from '@nestjs/common';
import { SupportAdmin } from 'src/support-admin/support-admin.entity';
import { ConfigService } from '@nestjs/config';

describe('AgencyService - deleteAgentById', () => {
  let agencyService: AgencyService;
  let userRepository: jest.Mocked<Partial<Repository<User>>>;
  let agentRepository: jest.Mocked<Partial<Repository<Agent>>>;
  let supportAdminRepository: jest.Mocked<Partial<Repository<SupportAdmin>>>;

  const agency = { id: 'agency-1' } as Agency;
  const agent = { userId: 'agent-1', agency } as Agent;
  const user = { id: 'agent-1' } as User;

  beforeEach(async () => {
    userRepository = {
      findOneBy: jest.fn(),
      delete: jest.fn(),
    };

    agentRepository = {
      findOne: jest.fn(),
    };

    supportAdminRepository = {};

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        AgencyService,
        { provide: getRepositoryToken(User), useValue: userRepository },
        { provide: getRepositoryToken(Agent), useValue: agentRepository },
        {
          provide: getRepositoryToken(SupportAdmin),
          useValue: supportAdminRepository,
        },
        { provide: getRepositoryToken(Agency), useValue: {} },
        { provide: getRepositoryToken(Object), useValue: {} },
        {
          provide: ConfigService,
          useValue: {
            get: jest.fn().mockReturnValue('test-value'),
          },
        },
      ],
    }).compile();

    agencyService = module.get<AgencyService>(AgencyService);
  });

  afterEach(() => jest.clearAllMocks());

  it('TC1 - It should properly delete an agent', async () => {
    // MOCK
    (userRepository.findOneBy as jest.Mock).mockResolvedValue(user);
    (agentRepository.findOne as jest.Mock).mockResolvedValue(agent);

    // ACT
    const result = await agencyService.deleteAgentById(agent.userId, agency.id);

    // ASSERT
    expect(userRepository.delete).toHaveBeenCalledWith(agent.userId);
    expect(result).toEqual({ message: 'Agent delete successfully' });
  });

  it('TC2 - Should throw NotFoundException if user does not exist', async () => {
    // MOCK
    (userRepository.findOneBy as jest.Mock).mockResolvedValue(null);

    // ACT && ASSERT
    await expect(
      agencyService.deleteAgentById('invalid-agent', agency.id),
    ).rejects.toThrow(NotFoundException);
  });

  it('TC3 - Should throw NotFoundException if agent does not exist', async () => {
    // MOCK
    (userRepository.findOneBy as jest.Mock).mockResolvedValue(user);
    (agentRepository.findOne as jest.Mock).mockResolvedValue(null);

    // ACT && ASSERT
    await expect(
      agencyService.deleteAgentById(agent.userId, agency.id),
    ).rejects.toThrow(NotFoundException);
  });

  it('TC4 - Should throw UnauthorizedException if the agency does not correspond', async () => {
    // MOCK
    (userRepository.findOneBy as jest.Mock).mockResolvedValue(user);
    (agentRepository.findOne as jest.Mock).mockResolvedValue({
      ...agent,
      agency: { id: 'agency-2' },
    });

    // ACT && ASSERT
    await expect(
      agencyService.deleteAgentById(agent.userId, agency.id),
    ).rejects.toThrow(UnauthorizedException);
  });
});

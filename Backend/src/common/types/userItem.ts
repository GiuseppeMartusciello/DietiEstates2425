import { Manager } from 'src/agency-manager/agency-manager.entity';
import { Agent } from 'src/agent/agent.entity';
import { User } from 'src/auth/user.entity';
import { Client } from 'src/client/client.entity';
import { SupportAdmin } from 'src/support-admin/support-admin.entity';

export class UserItem extends User {
  manager?: Manager;
  agent?: Agent;
  supportAdmin?: SupportAdmin;
  client?: Client;
}

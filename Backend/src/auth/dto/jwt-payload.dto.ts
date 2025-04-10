import { Roles } from 'src/common/types/roles';

export interface JwtPayload {
  userId: string;
  role: Roles;
}

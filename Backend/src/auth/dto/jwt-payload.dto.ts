import { UserRoles } from 'src/common/types/user-roles';

export interface JwtPayload {
  userId: string;
  role: UserRoles;
}

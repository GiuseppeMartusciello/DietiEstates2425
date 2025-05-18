import { SrvRecord } from 'dns';

export interface AuthResponse {
  accessToken: string;

  role: string;

  mustChangePassword: boolean;
}

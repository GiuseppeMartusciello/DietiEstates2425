import { SrvRecord } from 'dns';

export interface AuthResponse {
  accessToken: string;

  mustChangePassword: boolean;
}

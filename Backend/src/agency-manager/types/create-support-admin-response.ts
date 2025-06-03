export interface CreateSupportAdminResponse {
  message: string;
  supportAdmin: {
    id: string;
    email: string;
    name: string;
    agency: {
      id: string;
      name: string;
    };
  };
}

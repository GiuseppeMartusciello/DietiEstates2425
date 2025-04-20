export interface CreateAgentResponse {
  message: string;
  agent: {
    email: string;
    id: string;
    name: string;
    licenseNumber: string;
    agency: {
      id: string;
      name: string;
    };
  };
}

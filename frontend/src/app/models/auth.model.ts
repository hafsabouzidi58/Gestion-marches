export interface LoginRequest {
  email: string;
  motDePasse: string;
}

export interface AuthResponse {
  token: string;
  id: number;
  email: string;
  nom: string;
  prenom: string;
  role: string;
}

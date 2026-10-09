import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginRequest, AuthResponse } from '../models/auth.model';

export interface RegisterRequest {
  nom: string;
  prenom: string;
  email: string;
  motDePasse: string;
  role: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private AUTH_API = 'http://localhost:8081/api/auth/';

  constructor(private http: HttpClient) {}

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      this.AUTH_API + 'login',
      credentials
    );
  }

  register(user: RegisterRequest): Observable<string> {
    return this.http.post(
      this.AUTH_API + 'register',
      user,
      {
        responseType: 'text'
      }
    );
  }
}

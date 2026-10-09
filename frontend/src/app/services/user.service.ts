import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserDTO {
  id?: number;
  nom: string;
  prenom: string;
  email: string;
  motDePasseHash?: string; // 👈 Correspond exactement au champ backend
  role: string;
  actif: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8081/api/admin/users';
  private profileUrl = 'http://localhost:8081/api/profile';

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<UserDTO[]> {
    return this.http.get<UserDTO[]>(this.apiUrl);
  }

  searchByEmail(email: string): Observable<UserDTO[]> {
    return this.http.get<UserDTO[]>(`${this.apiUrl}/search?email=${email}`);
  }

  updateUser(id: number, user: UserDTO): Observable<UserDTO> {
    return this.http.put<UserDTO>(`${this.apiUrl}/${id}`, user);
  }
// ✅ Nouvelle méthode dédiée à la mise à jour du profil personnel
  updateProfile(id: number, user: Partial<UserDTO>): Observable<UserDTO> {
    return this.http.put<UserDTO>(`${this.profileUrl}/${id}`, user);
  }
  toggleUserStatus(id: number): Observable<UserDTO> {
    return this.http.put<UserDTO>(`${this.apiUrl}/${id}/toggle-status`, {});
  }
}

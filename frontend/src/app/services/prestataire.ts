import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Prestataire {
  id?: number;
  nomSociete: string;
  rc: string;
  patente: string;
  identifiantFiscal: string;
  banque: string;
  cle_rib?: string;
  cnss?: string;
  adresse?: string;
  telephone?: string;
  email?: string;
  actif?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class PrestataireService {
  private apiUrl = 'http://localhost:8081/api/prestataires';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Prestataire[]> {
    return this.http.get<Prestataire[]>(this.apiUrl);
  }

  getById(id: number): Observable<Prestataire> {
    return this.http.get<Prestataire>(`${this.apiUrl}/${id}`);
  }

  create(prestataire: Prestataire): Observable<Prestataire> {
    return this.http.post<Prestataire>(this.apiUrl, prestataire);
  }

  update(id: number, prestataire: Prestataire): Observable<Prestataire> {
    return this.http.put<Prestataire>(`${this.apiUrl}/${id}`, prestataire);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  toggleStatus(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/toggle-status`, {});
  }
}

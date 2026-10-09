import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Garantie } from '../models/garantie.model';

@Injectable({
  providedIn: 'root'
})
export class GarantieService {
  private apiUrl = 'http://localhost:8081/api/garanties';

  constructor(private http: HttpClient) {}

  getAllGaranties(): Observable<Garantie[]> {
    return this.http.get<Garantie[]>(this.apiUrl);
  }

  getGarantieById(id: number): Observable<Garantie> {
    return this.http.get<Garantie>(`${this.apiUrl}/${id}`);
  }

  getGarantiesByMarche(marcheId: number): Observable<Garantie[]> {
    return this.http.get<Garantie[]>(`${this.apiUrl}/marche/${marcheId}`);
  }

  createGarantie(garantie: Garantie): Observable<Garantie> {
    return this.http.post<Garantie>(this.apiUrl, garantie);
  }

  updateGarantie(id: number, garantie: Garantie): Observable<Garantie> {
    return this.http.put<Garantie>(`${this.apiUrl}/${id}`, garantie);
  }

  libererGarantie(id: number): Observable<Garantie> {
    return this.http.put<Garantie>(`${this.apiUrl}/${id}/liberer`, {});
  }

  deleteGarantie(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}

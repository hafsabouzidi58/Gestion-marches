import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Decompte } from '../models/decompte.model';

@Injectable({
  providedIn: 'root'
})
export class DecompteService {
  private apiUrl = 'http://localhost:8081/api/decomptes';

  constructor(private http: HttpClient) {}

  enregistrerDecompte(decompte: any): Observable<Decompte> {
    return this.http.post<Decompte>(this.apiUrl, decompte);
  }

  getByMarche(marcheId: number): Observable<Decompte[]> {
    return this.http.get<Decompte[]>(`${this.apiUrl}/marche/${marcheId}`);
  }

  getSoldeRestant(marcheId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/solde/${marcheId}`);
  }
}

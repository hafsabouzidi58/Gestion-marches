import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Déclaration de l'interface Nantissement directement dans le service
// src/app/models/nantissement.model.ts (ou dans votre service)
export interface Marche {
  id: number;
  numMarche: string; // Ou le nom exact de la propriété dans votre backend
}

export interface Nantissement {
  marcheId: number;
  marche?: Marche; // Objet marché imbriqué renvoyé par Spring Boot
  numMarche?: string;
  compteBancaire: string;
  banque: string;
  montantInitial: number;
  interetsMoratoires: number;
  sommeAValoire: number;
  cp: number;
  ce: number;
  montantCautionDefinitive: number;
  dateConstitutionCaution: string;
  dateLiberationCaution: string;
  montantRetenueGarantie: number;
  dateConstitutionRetenue: string;
  dateLiberationRetenue: string;
}

@Injectable({
  providedIn: 'root'
})
export class NantissementService {
  private baseUrl = 'http://localhost:8081/api';

  constructor(private http: HttpClient) {}

  searchNantissements(keyword: string = ''): Observable<Nantissement[]> {
    return this.http.get<Nantissement[]>(`${this.baseUrl}/nantissements/search?keyword=${keyword}`);
  }

  getByMarcheId(marcheId: number): Observable<Nantissement> {
    return this.http.get<Nantissement>(`${this.baseUrl}/nantissements/marche/${marcheId}`);
  }

  saveOrUpdate(marcheId: number, nantissement: Nantissement): Observable<Nantissement> {
    return this.http.post<Nantissement>(`${this.baseUrl}/nantissements/marche/${marcheId}`, nantissement);
  }

  deleteByMarcheId(marcheId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/nantissements/marche/${marcheId}`);
  }
}

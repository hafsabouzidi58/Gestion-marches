import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

// Définition de l'interface directement dans le service
export interface Marche {
  id?: number;
  numMarche: string;
  modePassation: string;
  objetMarche: string;
  dateApprobation: string;
  dateFinPrevue?: string;
  visaNumero: string;
  exercice: string;
budget?: number | string;
  article: string;
  paragraphe: string;
  ligne: string;
  rubrique: string;
  prestataireId?: number;
  nomPrestataire?: string;
  imputationId?: number;
  actif?: boolean;
  nomResponsable?: string;
    emailResponsable?: string;
    telephoneResponsable?: string;
    observations?: string;

}

@Injectable({
  providedIn: 'root'
})
export class MarcheService {
  private apiUrl = 'http://localhost:8081/api/marches';

  constructor(private http: HttpClient) {}

  // 1. Récupérer tous les marchés
  getAll(): Observable<Marche[]> {
    return this.http.get<Marche[]>(this.apiUrl);
  }

  // 2. Récupérer un marché par son ID
  getById(id: number): Observable<Marche> {
    return this.http.get<Marche>(`${this.apiUrl}/${id}`);
  }

  // 3. Créer un nouveau marché
  create(marche: Marche): Observable<Marche> {
    return this.http.post<Marche>(this.apiUrl, marche);
  }

  // 4. Mettre à jour un marché existant
  update(id: number, marche: Marche): Observable<Marche> {
    return this.http.put<Marche>(`${this.apiUrl}/${id}`, marche);
  }

  // 5. Supprimer un marché
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // 🔍 6. Recherche Multi-critères (par mot-clé)
  search(keyword: string): Observable<Marche[]> {
    const params = new HttpParams().set('keyword', keyword);
    return this.http.get<Marche[]>(`${this.apiUrl}/search`, { params });
  }

  // ⚠️ 7. Alerte Intelligente (marchés arrivant à échéance sous N jours)
  getAlertesEcheance(jours: number = 30): Observable<Marche[]> {
    const params = new HttpParams().set('jours', jours.toString());
    return this.http.get<Marche[]>(`${this.apiUrl}/alertes-echeance`, { params });
  }

  toggleStatus(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/toggle-status`, {});
  }
}

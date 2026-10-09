import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PieceJointe {
  id: number;
  nomFichier: string;
  typeFichier: string;
  url: string;
}

export interface PenalitePrediction {
  marcheId: number;
  numMarche: string;
  totalJoursExec: number;
  joursEcoules: number;
  avancementPrevuPct: number;
  avancementReelPct: number;
  ecartPct: number;
  joursRetardEstimes: number;
  montantBudget: number;
  montantPenaliteEstime: number;
  montantPlafondMax: number;
  plafondAtteint: boolean;
  alertePrioritaire: boolean;
}

export interface AvancementResponse {
  id: number;
  marcheId: number;
  numMarche: string;
  tauxPrevu: number;
  tauxReel: number;
  description: string;
  dateAvancement: string;
  createdAt: string;
  piecesJointes: PieceJointe[];
  predictionPenalite?: PenalitePrediction;
}

@Injectable({
  providedIn: 'root'
})
export class AvancementService {

  private apiUrl = 'http://localhost:8081/api/avancements';

  constructor(private http: HttpClient) {}

  // Charger l'historique d'un marché
  getHistorique(marcheId: number): Observable<AvancementResponse[]> {
    return this.http.get<AvancementResponse[]>(`${this.apiUrl}/marche/${marcheId}`);
  }

  // Rechercher des avancements par mot-clé
  rechercher(marcheId: number, keyword: string): Observable<AvancementResponse[]> {
    const params = new HttpParams().set('keyword', keyword);
    return this.http.get<AvancementResponse[]>(`${this.apiUrl}/marche/${marcheId}/search`, { params });
  }

  // Enregistrer un nouvel avancement avec fichiers
  enregistrer(avancementJson: any, fichiers: File[]): Observable<AvancementResponse> {
    const formData = new FormData();
    formData.append('avancement', JSON.stringify(avancementJson));
    fichiers.forEach(file => formData.append('fichiers', file));

    return this.http.post<AvancementResponse>(this.apiUrl, formData);
  }

  // Obtenir la simulation des pénalités
  getPrediction(marcheId: number): Observable<PenalitePrediction> {
    return this.http.get<PenalitePrediction>(`${this.apiUrl}/prediction/marche/${marcheId}`);
  }

  // Supprimer un avancement
  supprimer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Ajouter cette méthode dans AvancementService
  getMarches(): Observable<any[]> {
    return this.http.get<any[]>('http://localhost:8081/api/marches');
  }

  // Ajouter l'option responseType: 'blob'
  telechargerFichier(nomFichier: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/fichiers/${nomFichier}`, {
      responseType: 'blob'
    });
  }


  // Récupérer la prédiction des pénalités d'un marché
  getPredictionPenalites(marcheId: number): Observable<PenalitePrediction> {
    return this.http.get<PenalitePrediction>(`${this.apiUrl}/prediction/marche/${marcheId}`);
  }

  // Enregistrer un avancement (FormData pour les fichiers)
  enregistrerAvancement(formData: FormData): Observable<any> {
    return this.http.post<any>(this.apiUrl, formData);
  }

  // Récupérer les alertes globales pour le Dashboard
  getAlertesGlobales(): Observable<PenalitePrediction[]> {
    return this.http.get<PenalitePrediction[]>(`${this.apiUrl}/alertes`);
  }
getToutesLesAlertesPenalites(): Observable<PenalitePrediction[]> {
  // Corriger l'URL : remplacer 'alertes-penalites' par 'alertes'
  return this.http.get<PenalitePrediction[]>(`${this.apiUrl}/alertes`);
}
}

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProblemeCreateDTO, ProblemeResponseDTO, EtatProbleme ,MessageCreateDTO , MessageResponseDTO} from '../models/probleme.model';

export interface Marche {
  id: number;
  intitule: string;
  numMarche: string;
  numero?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProblemeService {

  private apiUrl = 'http://localhost:8081/api/problemes';
  private marcheUrl = 'http://localhost:8081/api/marches'; // Adapter selon votre endpoint des marchés

  constructor(private http: HttpClient) { }

  getMarches(): Observable<Marche[]> {
    return this.http.get<Marche[]>(this.marcheUrl);
  }

  declarerProbleme(
    dto: ProblemeCreateDTO,
    utilisateurId: number,
    fichiers: File[]
  ): Observable<ProblemeResponseDTO> {
    const formData = new FormData();
    formData.append('probleme', JSON.stringify(dto));

    if (fichiers && fichiers.length > 0) {
      for (let i = 0; i < fichiers.length; i++) {
        formData.append('fichiers', fichiers[i]);
      }
    }

    const params = new HttpParams().set('utilisateurId', utilisateurId.toString());
    return this.http.post<ProblemeResponseDTO>(this.apiUrl, formData, { params });
  }
  changerEtat(problemeId: number, nouvelEtat: EtatProbleme): Observable<ProblemeResponseDTO> {
    const params = new HttpParams().set('etat', nouvelEtat);
    return this.http.patch<ProblemeResponseDTO>(`${this.apiUrl}/${problemeId}/etat`, null, { params });
  }
  // Dans src/app/services/probleme.service.ts

  getAllProblemes(): Observable<ProblemeResponseDTO[]> {
    return this.http.get<ProblemeResponseDTO[]>(this.apiUrl);
  }
  // Récupérer / Télécharger un fichier par son ID
  downloadFichier(pieceJointeId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/fichiers/${pieceJointeId}`, {
      responseType: 'blob'
    });
  }
  getProblemesByUtilisateur(utilisateurId: number): Observable<ProblemeResponseDTO[]> {
    return this.http.get<ProblemeResponseDTO[]>(`${this.apiUrl}/utilisateur/${utilisateurId}`);
  }
  ajouterMessage(
    problemeId: number,
    dto: MessageCreateDTO,
    utilisateurId: number,
    fichiers: File[] = []
  ): Observable<MessageResponseDTO> {
    const formData = new FormData();
    formData.append('message', JSON.stringify(dto));
    formData.append('utilisateurId', utilisateurId.toString());

    // Ajouter chaque fichier sous la clé 'fichiers'
    if (fichiers && fichiers.length > 0) {
      for (let i = 0; i < fichiers.length; i++) {
        formData.append('fichiers', fichiers[i], fichiers[i].name);
      }
    }

    return this.http.post<MessageResponseDTO>(`${this.apiUrl}/${problemeId}/messages`, formData);
  }
}

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProblemeService, Marche } from '../../../services/probleme.service';
import { ProblemeResponseDTO, EtatProbleme, PieceJointeResponseDTO, MessageCreateDTO, MessageResponseDTO } from '../../../models/probleme.model';

@Component({
  selector: 'app-liste-problemes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './liste-problemes.component.html',
  styleUrls: ['./liste-problemes.component.css']
})
export class ListeProblemesComponent implements OnInit {

  problemes: ProblemeResponseDTO[] = [];
  problemesFiltres: ProblemeResponseDTO[] = [];
  marches: Marche[] = [];

  marcheFiltreId: string = '';
  etatFiltre: string = '';
  etats = Object.values(EtatProbleme);

  chargement: boolean = false;
  messageSucces: string = '';
  messageErreur: string = '';

  // 💬 Propriétés pour la gestion du Chat
  problemeChat: ProblemeResponseDTO | null = null;
  nouveauMessageTexte: string = '';
  fichiersMessage: File[] = [];
  enCoursEnvoiMessage: boolean = false;
  utilisateurConnecteId: number = 1; // Remplacer par l'ID de l'utilisateur connecté dynamiquement

  constructor(private problemeService: ProblemeService) {}

  ngOnInit(): void {
    this.chargerTousLesProblemes();
    this.chargerMarches();
  }

  chargerTousLesProblemes(): void {
    this.chargement = true;
    this.problemeService.getAllProblemes().subscribe({
      next: (data) => {
        this.problemes = data;
        this.appliquerFiltres();
        this.chargement = false;
      },
      error: (err) => {
        console.error(err);
        this.messageErreur = 'Erreur lors du chargement des problèmes.';
        this.chargement = false;
      }
    });
  }

  chargerMarches(): void {
    this.problemeService.getMarches().subscribe({
      next: (data) => {
        this.marches = data;
      },
      error: (err) => console.error(err)
    });
  }

  appliquerFiltres(): void {
    this.problemesFiltres = this.problemes.filter(p => {
      const matchMarche = !this.marcheFiltreId || p.marcheId === Number(this.marcheFiltreId);
      const matchEtat = !this.etatFiltre || p.etat === this.etatFiltre;
      return matchMarche && matchEtat;
    });
  }

  changerEtat(problemeId: number, event: Event): void {
    const nouvelEtat = (event.target as HTMLSelectElement).value as EtatProbleme;

    this.problemeService.changerEtat(problemeId, nouvelEtat).subscribe({
      next: (updatedProbleme) => {
        this.messageSucces = `L'état du problème N°${problemeId} a été mis à jour avec succès.`;
        const index = this.problemes.findIndex(p => p.id === problemeId);
        if (index !== -1) {
          this.problemes[index] = updatedProbleme;
          this.appliquerFiltres();
        }
        setTimeout(() => this.messageSucces = '', 4000);
      },
      error: (err) => {
        console.error(err);
        this.messageErreur = 'Erreur lors du changement d\'état.';
        setTimeout(() => this.messageErreur = '', 4000);
      }
    });
  }

  getBadgeColor(etat: EtatProbleme): string {
    switch (etat) {
      case EtatProbleme.EN_ATTENTE: return 'bg-warning text-dark';
      case EtatProbleme.EN_COURS_DE_TRAITEMENT: return 'bg-info text-dark';
      case EtatProbleme.RESOLU: return 'bg-success';
      case EtatProbleme.REJETE: return 'bg-danger';
      default: return 'bg-secondary';
    }
  }

  getPrioriteColor(priorite: string): string {
    switch (priorite) {
      case 'URGENTE': return 'text-danger fw-bold';
      case 'HAUTE': return 'text-warning fw-bold';
      default: return 'text-secondary';
    }
  }

  getNumeroMarche(marcheId: number): string {
    const marche = this.marches.find(m => m.id === marcheId);
    return marche ? marche.numMarche : marcheId.toString();
  }

  telechargerFichier(pj: PieceJointeResponseDTO): void {
    const nomFichier = pj.nomFichier || pj.nomOriginal || 'fichier-telecharge';

    this.problemeService.downloadFichier(pj.id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = nomFichier;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error('Erreur de téléchargement', err);
      }
    });
  }

  // 💬 Méthodes de gestion du Chat
  ouvrirChat(probleme: ProblemeResponseDTO): void {
    this.problemeChat = probleme;
    if (!this.problemeChat.messages) {
      this.problemeChat.messages = [];
    }
  }

  fermerChat(): void {
    this.problemeChat = null;
    this.nouveauMessageTexte = '';
    this.fichiersMessage = [];
  }

  onFichiersMessageSelect(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.fichiersMessage = Array.from(input.files);
    }
  }

  envoyerMessage(): void {
    const aDuTexte = this.nouveauMessageTexte && this.nouveauMessageTexte.trim().length > 0;
    const aDesFichiers = this.fichiersMessage && this.fichiersMessage.length > 0;

    if ((!aDuTexte && !aDesFichiers) || !this.problemeChat) {
      return;
    }

    this.enCoursEnvoiMessage = true;

    const dto: MessageCreateDTO = {
      contenu: aDuTexte ? this.nouveauMessageTexte.trim() : '📎 Pièce jointe'
    };

    this.problemeService.ajouterMessage(
      this.problemeChat.id,
      dto,
      this.utilisateurConnecteId,
      this.fichiersMessage
    ).subscribe({
      next: (msgResponse: MessageResponseDTO) => {
        if (!this.problemeChat!.messages) {
          this.problemeChat!.messages = [];
        }
        this.problemeChat!.messages.push(msgResponse);

        this.nouveauMessageTexte = '';
        this.fichiersMessage = [];

        const fileInput = document.getElementById('fichiersMessage') as HTMLInputElement;
        if (fileInput) {
          fileInput.value = '';
        }

        this.enCoursEnvoiMessage = false;
      },
      error: (err) => {
        console.error('Erreur lors de l\'envoi du message', err);
        this.enCoursEnvoiMessage = false;
      }
    });
  }
}

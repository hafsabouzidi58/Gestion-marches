import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AvancementService, AvancementResponse, PenalitePrediction } from '../../services/avancement.service';

@Component({
  selector: 'app-avancement',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './avancement.component.html'
})
export class AvancementComponent implements OnInit {

  @ViewChild('fileInput') fileInput!: ElementRef;

  marches: any[] = [];
  selectedMarcheId: number | null = null;

  historique: AvancementResponse[] = [];
  prediction?: PenalitePrediction;

  tauxReel: number = 0;
  description: string = '';
  fichiers: File[] = [];
  searchKeyword: string = '';

  constructor(private avancementService: AvancementService) {}

  ngOnInit(): void {
    this.chargerMarches();
  }

  chargerMarches(): void {
    this.avancementService.getMarches().subscribe(data => {
      this.marches = data;
    });
  }

  onMarcheChange(): void {
    if (this.selectedMarcheId) {
      this.selectedMarcheId = Number(this.selectedMarcheId);
      this.chargerHistorique();
      this.chargerPrediction();
    } else {
      this.historique = [];
      this.prediction = undefined;
    }
  }

  chargerHistorique(): void {
    if (!this.selectedMarcheId) return;
    this.avancementService.getHistorique(this.selectedMarcheId).subscribe(data => {
      this.historique = data;
    });
  }

  chargerPrediction(): void {
    if (!this.selectedMarcheId) return;
    this.avancementService.getPrediction(this.selectedMarcheId).subscribe(data => {
      this.prediction = data;
    });
  }

  // Cumule les nouveaux fichiers sélectionnés avec ceux déjà présents
  onFileChange(event: any): void {
    if (event.target.files && event.target.files.length > 0) {
      const nouveauxFichiers = Array.from(event.target.files) as File[];
      // Fusionne le tableau existant avec les nouveaux fichiers
      this.fichiers = [...this.fichiers, ...nouveauxFichiers];

      // Réinitialise l'input pour permettre de resélectionner le même fichier si besoin
      if (this.fileInput) {
        this.fileInput.nativeElement.value = '';
      }
    }
  }

// Permet à l'utilisateur de retirer un fichier de la liste avant envoi
  retirerFichier(index: number): void {
    this.fichiers.splice(index, 1);
  }

  onSearch(): void {
    if (!this.selectedMarcheId) return;
    this.avancementService.rechercher(this.selectedMarcheId, this.searchKeyword).subscribe(data => {
      this.historique = data;
    });
  }

  soumettreFormulaire(): void {
    if (!this.selectedMarcheId) return;

    const payload = {
      marcheId: this.selectedMarcheId,
      tauxReel: this.tauxReel,
      description: this.description
    };

    this.avancementService.enregistrer(payload, this.fichiers).subscribe(() => {
      this.tauxReel = 0;
      this.description = '';
      this.fichiers = [];

      // Réinitialise la valeur visuelle du champ <input type="file">
      if (this.fileInput) {
        this.fileInput.nativeElement.value = '';
      }

      this.chargerHistorique();
      this.chargerPrediction();
    });
  }

  supprimerAvancement(id: number): void {
    if (confirm('Voulez-vous vraiment supprimer cet avancement ?')) {
      this.avancementService.supprimer(id).subscribe(() => {
        this.chargerHistorique();
        this.chargerPrediction();
      });
    }
  }

  ouvrirFichier(nomFichier: string): void {
    this.avancementService.telechargerFichier(nomFichier).subscribe({
      next: (blob: Blob) => {
        const windowUrl = URL.createObjectURL(blob);
        window.open(windowUrl, '_blank');
      },
      error: (err) => console.error('Erreur lors de la lecture du fichier', err)
    });
  }
}

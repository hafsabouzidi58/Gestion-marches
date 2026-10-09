import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { PrestataireService, Prestataire } from '../../services/prestataire';

@Component({
  selector: 'app-green-wood-edit-prestataire',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './green-wood-edit-prestataire.html',
  styleUrl: './green-wood-edit-prestataire.css'
})
export class GreenWoodEditPrestataireComponent implements OnInit {
  prestataire: Prestataire | null = null;
  isLoading: boolean = true;

  constructor(
    private prestataireService: PrestataireService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadPrestataire();
  }

  loadPrestataire(): void {
    this.isLoading = true;

    // Récupère la liste globale puis filtre sur 'green_wood'
    this.prestataireService.getAll().subscribe({
      next: (prestataires: Prestataire[]) => {
        const gw = prestataires.find(p =>
          p.nomSociete && p.nomSociete.toLowerCase().trim() === 'green_wood'
        );

        if (gw) {
          this.prestataire = gw;
        } else {
          console.warn('Prestataire green_wood non trouvé dans la liste');
        }
        this.isLoading = false;
      },
      error: (err: unknown) => {
        console.error('Erreur chargement des prestataires', err);
        this.isLoading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.prestataire && this.prestataire.id) {
      this.prestataireService.update(this.prestataire.id, this.prestataire).subscribe({
        next: () => {
          alert('Informations du prestataire mises à jour avec succès !');
          this.router.navigate(['/green-wood/dashboard']);
        },
        error: (err: unknown) => {
          console.error('Erreur lors de la mise à jour', err);
          alert('Échec de la mise à jour des informations.');
        }
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/green-wood/dashboard']);
  }
}

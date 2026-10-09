import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Prestataire, PrestataireService } from '../../../services/prestataire';

@Component({
  selector: 'app-prestataire-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './prestataire-form.html',
  styleUrls: ['./prestataire-form.css']
})
export class PrestataireFormComponent implements OnInit {
  isEditMode: boolean = false;
  prestataireId?: number;

  prestataire: Prestataire = {
    nomSociete: '',
    rc: '',
    patente: '',
    identifiantFiscal: '',
    cnss: '',
    banque: '',
    cle_rib: '',
    adresse: '',
    actif: true
  };

  constructor(
    private prestataireService: PrestataireService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.prestataireId = +idParam;
      this.loadPrestataire(this.prestataireId);
    }
  }

  loadPrestataire(id: number): void {
    this.prestataireService.getById(id).subscribe({
      next: (data: any) => {
        // Hydratation de l'objet avec gestion explicite du RIB et du Nom Société
        this.prestataire = {
          ...data,
          nomSociete: data.nomSociete || data.nom || '',
          cle_rib: data.cle_rib || data.cle_rib || ''
        };
      },
      error: (err: any) => console.error('Erreur lors du chargement du prestataire :', err)
    });
  }

  onSubmit(): void {
    if (this.isEditMode && this.prestataireId) {
      this.prestataireService.update(this.prestataireId, this.prestataire).subscribe({
        next: () => this.router.navigate(['/responsable-marches/prestataires']),
        error: (err: any) => console.error('Erreur lors de la modification :', err)
      });
    } else {
      this.prestataireService.create(this.prestataire).subscribe({
        next: () => this.router.navigate(['/responsable-marches/prestataires']),
        error: (err: any) => console.error('Erreur lors de l\'ajout :', err)
      });
    }
  }
}

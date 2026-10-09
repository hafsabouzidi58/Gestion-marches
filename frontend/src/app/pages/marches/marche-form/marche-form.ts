import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { PrestataireService } from '../../../services/prestataire';
import { MarcheService, Marche } from '../../../services/marche.service';
@Component({
  selector: 'app-marche-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './marche-form.html',
  styleUrls: ['./marche-form.css']
})
export class MarcheFormComponent implements OnInit {
  isEditMode: boolean = false;
  marcheId?: number;
  prestataires: any[] = [];

  marche: Marche = {
    numMarche: '',
    modePassation: 'Appel d\'offres ouvert',
    objetMarche: '',
    dateApprobation: '',
    dateFinPrevue: '',
    visaNumero: '',
    exercice: new Date().getFullYear().toString(),
    budget: '',
    article: '',
    paragraphe: '',
    ligne: '',
    rubrique: ''
  };

  constructor(
    private marcheService: MarcheService,
    private prestataireService: PrestataireService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadPrestataires();

    this.marcheId = this.route.snapshot.params['id'];
    if (this.marcheId) {
      this.isEditMode = true;
      this.marcheService.getById(this.marcheId).subscribe({
        next: (data) => (this.marche = data),
        error: (err) => console.error('Erreur de chargement du marché', err)
      });
    }
  }

  loadPrestataires(): void {
    this.prestataireService.getAll().subscribe({
      next: (data: any[]) => {
        console.log('Prestataires récupérés du backend :', data);
        this.prestataires = data;
      },
      error: (err: any) => console.error('Erreur lors du chargement des prestataires :', err)
    });
  }

  onSubmit(): void {
    if (this.isEditMode && this.marcheId) {
      this.marcheService.update(this.marcheId, this.marche).subscribe({
        next: () => this.router.navigate(['/responsable-marches/marches']),
        error: (err) => console.error('Erreur modification', err)
      });
    } else {
      this.marcheService.create(this.marche).subscribe({
        next: () => this.router.navigate(['/responsable-marches/marches']),
        error: (err) => console.error('Erreur création', err)
      });
    }
  }
}

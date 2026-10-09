import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-green-wood-layout',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './responsable-green-wood-layout.html',
  styleUrls: ['./responsable-green-wood-layout.css']
})
export class GreenWoodLayoutComponent {

  constructor(private router: Router) {}

  logout(): void {
    // Supprimer la session ou les tokens si nécessaire
    localStorage.removeItem('token');
    sessionStorage.clear();

    // Rediriger vers la page de connexion
    this.router.navigate(['/login']);
  }
}

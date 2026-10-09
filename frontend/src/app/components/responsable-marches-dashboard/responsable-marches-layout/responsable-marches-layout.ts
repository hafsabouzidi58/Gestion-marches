import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, RouterOutlet } from '@angular/router'; // 👈 1. Ajouter RouterOutlet ici

import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-responsable-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, RouterOutlet], // 👈 2. Ajouter RouterOutlet ici
  templateUrl: './responsable-marches-layout.html',
  styleUrls: ['./responsable-marches-layout.css']
})
export class ResponsableLayoutComponent {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  logout() {
    localStorage.clear(); // Préférer clear() pour nettoyer 'user' et 'token'
    this.router.navigate(['/login']);
  }
}

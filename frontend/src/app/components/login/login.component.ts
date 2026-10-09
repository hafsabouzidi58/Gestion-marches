import { Component } from '@angular/core';
import { CommonModule } from '@angular/common'; // Pour *ngIf
import { FormsModule } from '@angular/forms';   // Pour [(ngModel)]
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { StorageService } from '../../services/storage.service';

@Component({
  selector: 'app-login',
  standalone: true, // Component Standalone
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  credentials = { email: '', motDePasse: '' };
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private storageService: StorageService,
    private router: Router
  ) {}

  onSubmit(): void {
    this.authService.login(this.credentials).subscribe({
      next: (data) => {
        this.storageService.saveUser(data);
        this.redirectByRole(data.role);
      },
      error: (err) => {
        this.errorMessage = 'Identifiants invalides ou compte inactif';
      }
    });
  }

  private redirectByRole(role: string): void {
    switch (role) {
      case 'ADMIN_SYSTEME':
        this.router.navigate(['/admin/dashboard']);
        break;
      case 'RESPONSABLE_MARCHES':
        this.router.navigate(['/responsable-marches/dashboard']);
        break;
      case 'RESPONSABLE_GREEN_WOOD':
        this.router.navigate(['/RESPONSABLE_GREEN_WOOD/dashboard']);
        break;
      default:
        this.router.navigate(['/unknown/dashboard']);
        break;
    }
  }
}

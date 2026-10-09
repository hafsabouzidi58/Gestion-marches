import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

const USER_KEY = 'user';

@Injectable({
  providedIn: 'root'
})
export class StorageService {

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {}

  public saveUser(user: any): void {
    if (isPlatformBrowser(this.platformId)) {
      window.localStorage.removeItem(USER_KEY);
      window.localStorage.setItem(USER_KEY, JSON.stringify(user));
      if (user?.token) {
        window.localStorage.setItem('token', user.token);
      }
    }
  }

  public getUser(): any {
    if (isPlatformBrowser(this.platformId)) {
      const user = window.localStorage.getItem(USER_KEY);
      if (user) {
        try {
          return JSON.parse(user);
        } catch (e) {
          return null;
        }
      }
    }
    return null;
  }

  public getToken(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return window.localStorage.getItem('token');
    }
    return null;
  }

  public clean(): void {
    if (isPlatformBrowser(this.platformId)) {
      window.localStorage.clear();
    }
  }
}

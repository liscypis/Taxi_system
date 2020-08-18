import { Injectable } from '@angular/core';

const TOKEN_KEY = 'token';
const USER_KEY = 'user-details';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {

  constructor() { }

  signOut(): void {
    window.sessionStorage.clear();
  }

  public saveJWTToken(token: string): void {
    window.sessionStorage.removeItem(TOKEN_KEY);
    window.sessionStorage.setItem(TOKEN_KEY, token);
  }

  public getJWTToken(): string {
    return sessionStorage.getItem(TOKEN_KEY);
  }

  public saveUserDetails(user): void {
    window.sessionStorage.removeItem(USER_KEY);
    window.sessionStorage.setItem(USER_KEY, JSON.stringify(user));
  }

  public getUserDetails(): any {
    return JSON.parse(sessionStorage.getItem(USER_KEY));
  }
}

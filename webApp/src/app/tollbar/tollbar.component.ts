import { Component, Input } from '@angular/core';
import {TokenStorageService} from '../services/token-storage.service'
import { from } from 'rxjs';


@Component({
  selector: 'app-tollbar',
  templateUrl: './tollbar.component.html',
  styleUrls: ['./tollbar.component.css']
})
export class TollbarComponent {
  private roles: string[];
  isLoggedIn = false;
  admin = false;
  dispatcher = false;
  name :String
  surname :String;

  constructor(private tokenStorageService: TokenStorageService) {}


ngOnInit(): void {
    this.isLoggedIn = !!this.tokenStorageService.getJWTToken();

    if (this.isLoggedIn) {
      const user = this.tokenStorageService.getUserDetails();
      this.roles = user.roles;

      this.admin = this.roles.includes('ROLE_ADMIN');
      this.dispatcher = this.roles.includes('ROLE_DISPATCHER');

      this.name = user.name;
      this.surname = user.surname;
    }
  }

  logout(): void {
    this.tokenStorageService.signOut();
    window.location.reload();
  }
  

}

import { Component, OnInit } from '@angular/core';
import { TokenStorageService } from '../services/token-storage.service';

@Component({
  selector: 'app-logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.css']
})
export class LogoutComponent implements OnInit {

  constructor(private tokenStorageService: TokenStorageService) { }

  ngOnInit(): void {
    if(this.tokenStorageService.getJWTToken()){
      this.logout();
      this.reloadPage();
    }
  }


  logout(): void {
    this.tokenStorageService.signOut();
    // window.location.reload();
  }
  reloadPage(): void {
    window.location.reload();
  }
}

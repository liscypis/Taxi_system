import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import {LoginRequest} from '../models/LoginReques';
import {AuthServiceService} from '../services/auth-service.service';
import {TokenStorageService} from '../services/token-storage.service'
import { Router } from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  data = new LoginRequest()
  errorMessage = '';

  constructor(private tokenStorageService: TokenStorageService,
    private authService: AuthServiceService,
    private router: Router) { }

  ngOnInit(): void {
    
  }

  login() :void {
    console.log(this.data)
    this.authService.login(this.data).subscribe(
      data => {
        console.log(data)
        this.tokenStorageService.saveJWTToken(data.accessToken);
        this.tokenStorageService.saveUserDetails(data);
        this.loadPage();
      },
      err => {
        this.errorMessage = err.error.message;
      }
    );
  }

  reloadPage(): void {
    window.location.reload();
  }

  loadPage():void {
    this.router.navigate(["/newRide"])
  }

}

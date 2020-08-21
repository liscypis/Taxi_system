import { Component, OnInit } from '@angular/core';
import { TokenStorageService } from '../services/token-storage.service';
import { FormBuilder, Validators, FormGroup, FormControl } from '@angular/forms';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {

  isLoggedIn = false;

  dataForm = new FormGroup({
    name: new FormControl('', [ Validators.min(4), Validators.required]),
    surname: new FormControl('', [ Validators.min(5), Validators.required]),
    login: new FormControl('', [ Validators.min(4), Validators.required]),
    password: new FormControl('', [ Validators.min(5), Validators.required]),
    email: new FormControl('', [ Validators.min(6), Validators.required]),
    phone: new FormControl('', [ Validators.min(9),Validators.min(9), Validators.required]),
    role: new FormControl('', [Validators.required])
  })


  constructor(private tokenStorageService: TokenStorageService) { }

  ngOnInit(): void {
    if(this.tokenStorageService.getJWTToken()){
      this.isLoggedIn = true;
    }
  }

  onSubmit() {
    console.warn(this.dataForm);
    // this.data.angle = this.dataForm.value.angle;
    // this.data.quantity = this.dataForm.value.quantity;
    // this.data.size = this.dataForm.value.size;
    // this.data.lng = this.dataForm.value.lng;
    // this.data.lat = this.dataForm.value.lat;
    // this.data.watt = this.dataForm.value.watt;
    // this.data.loss = this.dataForm.value.loss;
    // console.log('Form data:', this.data);
  }


}

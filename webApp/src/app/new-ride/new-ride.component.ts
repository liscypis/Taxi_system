import { Component, OnInit } from '@angular/core';
import { DriverInfoResponse } from '../models/DriverInfoResponse'
import { RideConfirm } from '../models/RideConfirm'
import { formatDate } from "@angular/common";

import { RideRequest } from '../models/RideRequest'
import { APIService } from '../services/api.service'


@Component({
  selector: 'app-new-ride',
  templateUrl: './new-ride.component.html',
  styleUrls: ['./new-ride.component.css']
})
export class NewRideComponent implements OnInit {

  carInfo = new DriverInfoResponse();
  rideRequest = new RideRequest();
  rideconfirm = new RideConfirm();

  price :number;
  userTime: String;
  destiantionTime: String;
  rideId: number;

  rideInfo = false;
  driverInfo = false;
  errorMessage: any;




  constructor(private apiService: APIService) { }

  ngOnInit(): void {
  }

  initOrder(): void {
    if (this.rideRequest.destination == null || this.rideRequest.origin == null || this.rideRequest.phoneNumber == null) {
      this.errorMessage = "Uzupełnij wyszystkie pola";
      return;
    }
    console.log(this.rideRequest)
    this.apiService.initRide(this.rideRequest).subscribe(
      data => {
        console.log(data)
        this.rideId = data.idRide;
        this.price = data.approxPrice;
        this.userTime = this.setTime(data.driverDuration);
        this.destiantionTime = this.setTime(data.userDuration + data.driverDuration);

        this.rideInfo = true;

        console.log(this.userTime);
        console.log(this.destiantionTime);
      },
      err => {
        this.errorMessage = err.error.message;
      }
    );
  }

  confirmRide() :void {
    this.rideconfirm.idRide = this.rideId;
    this.rideconfirm.confirm = true;
    this.apiService.confirmRide(this.rideconfirm).subscribe(
      data => {
        console.log(data)
        this.carInfo = data;

        this.rideInfo = false;
        this.driverInfo = true;
      },
      err => {
        this.errorMessage = err.error.message;
        console.log(this.errorMessage);
      }
    );
  }

  cancelRide() :void {
    this.rideconfirm.idRide = this.rideId;
    this.rideconfirm.confirm = false;
    this.apiService.confirmRide(this.rideconfirm).subscribe(
      data => {
        console.log(data)
        
        this.rideInfo = false;
        this.driverInfo = false;
      },
      err => {
        this.errorMessage = err.error.message;
        console.log(this.errorMessage);
      }
    );
  }

  setTime(sec: number) {
    let time = new Date();
    time = new Date(time.getTime() + sec * 1000);
    const format = 'HH:mm';
    const locale = 'en-US';
    const formattedDate = formatDate(time, format, locale);
    return formattedDate;
  }
  reloadPage(): void {
    window.location.reload();
  }
}

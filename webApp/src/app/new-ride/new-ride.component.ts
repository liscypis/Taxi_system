import { Component, OnInit } from '@angular/core';
import {DriverInfoResponse } from '../models/DriverInfoResponse'
import {RideDetailsResponse} from '../models/RideDetailsResponse'
import{RideRequest} from '../models/RideRequest'
import {APIService} from '../services/api.service'


@Component({
  selector: 'app-new-ride',
  templateUrl: './new-ride.component.html',
  styleUrls: ['./new-ride.component.css']
})
export class NewRideComponent implements OnInit {

  carInfo = new DriverInfoResponse();
  rideDetails = new RideDetailsResponse();
  rideRequest = new RideRequest()
  
  rideInfo = false;
  driverInfo = false;
  errorMessage: any;
  


  constructor(private apiService: APIService) { }

  ngOnInit(): void {
  }

  initOrder() :void {
    console.log(this.rideRequest)
    this.apiService.initRide(this.rideRequest).subscribe(
      data => {
        console.log(data)
      },
      err => {
        this.errorMessage = err.error.message;
      }
    );
  }

}

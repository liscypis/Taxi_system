import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { RideConfirm } from '../models/RideConfirm';
import { RideRequest } from '../models/RideRequest';
import { RegisterRequest } from '../models/RegisterRequest';

const API_URL = 'http://localhost:8081/api/test/';


@Injectable({
  providedIn: 'root'
})
export class APIService {

  constructor(private http: HttpClient) { }

  initRide(request: RideRequest): Observable<any> {
    console.log(request)
    return this.http.post(API_URL + 'initialOrderRideByDispatcher', request);
  }

  confirmRide(request: RideConfirm): Observable<any> {
    console.log(request)
    return this.http.post(API_URL + 'confirmRide', request);
  }

  getActiveRides(): Observable<any> {
    return this.http.get(API_URL + 'getActiveRides');
  }

  getDriverLocation(driverId: number): Observable<any> {
    return this.http.get(API_URL + 'getDriverLocation/' + driverId);
  }

  getCompleteRides(): Observable<any> {
    return this.http.get(API_URL + 'getCompleteRides');
  }

  getDriverRating(driverId: number): Observable<any> {
    return this.http.get(API_URL + 'getAvgRating/' + driverId);
  }
}

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { RideConfirm } from '../models/RideConfirm';
import { RideRequest } from '../models/RideRequest';
import { RegisterRequest } from '../models/RegisterRequest';
import { NewCarRequest } from '../models/NewCarRequest';
import { CarInfo } from '../models/CarInfo';

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

  addCar(request: NewCarRequest): Observable<any> {
    console.log(request)
    return this.http.post(API_URL + 'addCar', request);
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

  getDriverWithoutCar(): Observable<any> {
    return this.http.get(API_URL + 'driverWithoutCar');
  }

  getAllCars(): Observable<any> {
    return this.http.get(API_URL + 'getAllCars');
  }

  updateCar(request: CarInfo): Observable<any> {
    console.log(request)
    return this.http.post(API_URL + 'editCar', request);
  }

  deleteCar(idCar: number): Observable<any> {
    console.log(idCar)
    return this.http.delete(API_URL + 'deleteCar/' + idCar);
  }

  
}

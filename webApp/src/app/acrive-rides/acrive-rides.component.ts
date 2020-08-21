import { Component, OnInit, ViewChild } from '@angular/core';
import { APIService } from '../services/api.service';
import { RideDetailsResponse } from '../models/RideDetailsResponse'
import { MapInfoWindow, MapMarker, GoogleMap } from '@angular/google-maps';
import { Subscription, timer } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { TokenStorageService } from '../services/token-storage.service';



@Component({
  selector: 'app-acrive-rides',
  templateUrl: './acrive-rides.component.html',
  styleUrls: ['./acrive-rides.component.css']
})
export class AcriveRidesComponent implements OnInit {
  @ViewChild(GoogleMap, { static: false }) map: GoogleMap;
  @ViewChild(MapInfoWindow, { static: false }) info: MapInfoWindow;

  displayedColumns: string[] = ['idRide', 'driverPhone', 'userPhone'];
  dataSource: Array<RideDetailsResponse>;
  isData = false;

  isLoggedIn = false;

  subscription: Subscription;

  polylines = [];
  markers = [];
  infoContent = ''

  lat = 50.881505;
  lng = 20.652226;
  zoom = 12;
  center = new google.maps.LatLng(this.lat, this.lng);
  options: google.maps.MapOptions = {
    disableDefaultUI: true,
    fullscreenControl: true,
    zoomControl: false,
    maxZoom: 18,
    minZoom: 8,
  }
  constructor(private apiService: APIService,
    private tokenStorageService: TokenStorageService) { }


  ngOnInit(): void {
    if (this.tokenStorageService.getJWTToken()) {
      this.getAcriveRides();
      this.isLoggedIn = true;
      console.log(this.tokenStorageService.getJWTToken());
    }
  }


  getAcriveRides(): void {
    this.apiService.getActiveRides().subscribe(
      data => {
        console.log(data);
        this.dataSource = data;
        this.isData = true;
      },
      err => {
        // this.errorMessage = err.error.message;
        console.log(err.error.message);
      }
    );
  }
  onRowClicked(row) {
    console.log('Row clicked: ', row);
    this.polylines = [];
    this.markers = [];
    this.addMarker(row.userDestination, 'C', "Punkt końcowy");
    this.addMarker(row.userLocation, 'S', "Punkt początkowy");
    this.addPolyline(row.userPolyline, "purple");
    this.addPolyline(row.driverPolyline, "blue");
    this.getDriverLocation(Number(row.idDriver));
  }

  addPolyline(polyline: String, color: String) {
    var decodedPath: google.maps.LatLng[]
    decodedPath = google.maps.geometry.encoding.decodePath(String(polyline));
    console.log(decodedPath);

    this.polylines.push({
      path: decodedPath,
      polylineOptions: {
        strokeColor: color,
        strokeOpacity: 0.8
      }
    })
  }
  addMarker(row: String, type: String, info: String) {
    if (this.markers.length == 3) {
      this.markers.pop();
    }

    let splitetLoc = row.split(",");
    let pinImage = "";
    if (type == 'S')
      pinImage = "http://www.googlemapsmarkers.com/v1/S/009900/";
    if (type == 'T')
      pinImage = "http://www.googlemapsmarkers.com/v1/T/0099FF/";
    if (type == 'C')
      pinImage = "http://www.googlemapsmarkers.com/v1/C/FF0000/";

    this.markers.push({
      position: new google.maps.LatLng(Number(splitetLoc[0]), Number(splitetLoc[1])),
      options: {
        icon: pinImage
      },
      info: info
    })
    console.log(splitetLoc)
  }

  openInfo(marker: MapMarker, info) {
    this.infoContent = info;
    this.info.open(marker);
  }

  getDriverLocation(driverId: number): void {
    if (this.subscription != null)
      this.subscription.unsubscribe();

    this.subscription = timer(0, 10000).pipe(
      switchMap(() => this.apiService.getDriverLocation(driverId))).subscribe(
        data => {
          console.log(data);
          this.addMarker(data.msg, 'T', "Kierowca");
        },
        err => {
          // this.errorMessage = err.error.message;
          console.log(err.error.message);
        }
      );
  }

  ngOnDestroy() {
    if (this.subscription instanceof Subscription)
      this.subscription.unsubscribe();
  }
}

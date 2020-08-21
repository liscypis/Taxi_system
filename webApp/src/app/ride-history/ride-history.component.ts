import { Component, OnInit, ViewChild } from '@angular/core';
import { APIService } from '../services/api.service';
import { Ride } from '../models/Ride'
import { GoogleMap, MapInfoWindow, MapMarker } from '@angular/google-maps';
import { TokenStorageService } from '../services/token-storage.service';


@Component({
  selector: 'app-ride-history',
  templateUrl: './ride-history.component.html',
  styleUrls: ['./ride-history.component.css']
})
export class RideHistoryComponent implements OnInit {
  @ViewChild(GoogleMap, { static: false }) map: GoogleMap;
  @ViewChild(MapInfoWindow, { static: false }) info: MapInfoWindow;

  isLoggedIn = false;

  displayedColumns: string[] = ['idRide', 'idDriver', 'driverName', 'driverSurname', 'userName', 
  'userSurname', 'rating', 'price', 'timeStart', 'arrivalTime', 'endTime'];
  dataSource: Array<Ride>;

  idDriver :number;
  name:String;
  surname:String;
  avgRating:String;

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
      this.isLoggedIn = true;
      this.getCompleteRides();
    }
  }

  getCompleteRides(): void {
    this.apiService.getCompleteRides().subscribe(
      data => {
        console.log(data);
        this.dataSource = data;
        console.warn(this.dataSource);
      },
      err => {
        // this.errorMessage = err.error.message;
        console.log(err.error.message);
      }
    );
  }

  getDriverRating(idDriver:number):void {
    this.apiService.getDriverRating(idDriver).subscribe(
      data => {
        console.log(data);
        this.avgRating = data.msg;
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
    this.addPolyline(row.userPolyline, "purple", false);
    this.addPolyline(row.driverPolyline, "blue", true);

    this.idDriver = row.idDriver;
    this.name = row.driverName;
    this.surname = row.driverSurname;

    this.getDriverRating(Number(row.idDriver));
  }

  addPolyline(polyline: String, color: String, isToUserPoly:boolean) {
    var decodedPath: google.maps.LatLng[]
    decodedPath = google.maps.geometry.encoding.decodePath(String(polyline));
    console.log(decodedPath);
    if(isToUserPoly)
      this.addDriverMarekr(decodedPath[0]);

    this.polylines.push({
      path: decodedPath,
      polylineOptions: {
        strokeColor: color,
        strokeOpacity: 0.8
      }
    })
  }

  addDriverMarekr(loc: google.maps.LatLng) {
      let pinImage = "http://www.googlemapsmarkers.com/v1/T/0099FF/";

    this.markers.push({
      position: loc,
      options: {
        icon: pinImage
      },
      info: "Kierowca"
    })
  }


  addMarker(row: String, type: String, info: String) {
    let splitetLoc = row.split(",");
    let pinImage = "";
    if (type == 'S')
      pinImage = "http://www.googlemapsmarkers.com/v1/S/009900/";
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

}

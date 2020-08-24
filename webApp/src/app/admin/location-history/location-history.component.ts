import { Component, OnInit } from '@angular/core';
import { DriverPosition } from '../../models/DriverPosition';
import { UpdateUser } from 'src/app/models/UpdateUser';
import { APIService } from 'src/app/services/api.service';

@Component({
  selector: 'app-location-history',
  templateUrl: './location-history.component.html',
  styleUrls: ['./location-history.component.css']
})
export class LocationHistoryComponent implements OnInit {
  displayedLocationColumns: string[] = ['data', 'location'];
  locationDataSource: Array<DriverPosition>;

  displayedDiverColumns: string[] = ['id', 'name', 'surname'];
  driverDataSource: Array<UpdateUser>;

  selectedRow: boolean;
  selectedLocRow: boolean;

  markers = []
  zoom = 13;
  lat = 50.881505;
  lng = 20.652226;
  center = new google.maps.LatLng(this.lat, this.lng);
  options: google.maps.MapOptions = {
    disableDefaultUI: true,
    fullscreenControl: true,
    zoomControl: false,
    maxZoom: 18,
    minZoom: 8,
  }

  constructor(private api: APIService) { }

  ngOnInit(): void {
    this.getDrivers();
  }


  getDrivers(): void {
    this.api.getUserByRole('driver').subscribe(
      data => {
        console.log(data);
        this.driverDataSource = data;
      },
      err => {
        console.log(err.error.message);
      }
    );
  }

  getDriversLoc(driverId: number): void {
    this.api.getDriverLocations(driverId).subscribe(
      data => {
        console.log(data);
        this.locationDataSource = data;
      },
      err => {
        console.log(err.error.message);
      }
    );
  }

  onRowDriverClicked(row): void {
    this.getDriversLoc(row.id);
    if (!this.selectedRow)
      this.selectedRow = row;
    else
      this.selectedRow = row;
  }

  onRowLocClicked(row): void {
    if (!this.selectedLocRow)
      this.selectedLocRow = row;
    else
      this.selectedLocRow = row;
    this.addDriverMarekr(row.location)
  }

  addDriverMarekr(loc: String) {
    this.markers = [];
    let splitetLoc = loc.split(",");
    let pinImage = "http://www.googlemapsmarkers.com/v1/0099FF/";
    let location = new google.maps.LatLng(Number(splitetLoc[0]), Number(splitetLoc[1]));

    this.center = location;

    this.markers.push({
      position: location,
      options: {
        icon: pinImage
      },
      info: "Kierowca"
    })
  }
}

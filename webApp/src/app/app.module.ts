import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatSliderModule } from '@angular/material/slider';
import { TollbarComponent } from './tollbar/tollbar.component';
import { LayoutModule } from '@angular/cdk/layout';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {MatCardModule} from '@angular/material/card';
import {MatInputModule} from '@angular/material/input';
import { AdminComponent } from './admin/admin.component';
import { LoginComponent } from './login/login.component';
import { HttpClientModule } from '@angular/common/http';
import { NewRideComponent } from './new-ride/new-ride.component';
import {MatGridListModule} from '@angular/material/grid-list';
import { AcriveRidesComponent } from './acrive-rides/acrive-rides.component';
import { GoogleMapsModule } from '@angular/google-maps';
import {MatTableModule} from '@angular/material/table';
import {CdkScrollableModule} from '@angular/cdk/scrolling';
import { ScrollingModule } from '@angular/cdk/scrolling';
import { RideHistoryComponent } from './ride-history/ride-history.component';
import { LogoutComponent } from './logout/logout.component';
import {MatTabsModule} from '@angular/material/tabs';
import {MatRadioModule} from '@angular/material/radio';
import { EditCarComponent } from './admin/edit-car/edit-car.component';
import { EditUserComponent } from './admin/edit-user/edit-user.component';
import { MatSelectModule } from '@angular/material/select';
import { LocationHistoryComponent } from './admin/location-history/location-history.component';


@NgModule({
  declarations: [
    AppComponent,
    TollbarComponent,
    AdminComponent,
    LoginComponent,
    NewRideComponent,
    AcriveRidesComponent,
    RideHistoryComponent,
    LogoutComponent,
    EditCarComponent,
    EditUserComponent,
    LocationHistoryComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule, 
    MatSliderModule,
    LayoutModule,
    MatToolbarModule,
    MatButtonModule,
    MatSidenavModule,
    MatIconModule,
    MatListModule,
    FormsModule,
    MatCardModule,
    MatInputModule,
    MatGridListModule,
    GoogleMapsModule,
    MatTableModule,
    CdkScrollableModule,
    ScrollingModule,
    MatTabsModule,
    MatRadioModule,
    ReactiveFormsModule,
    MatSelectModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

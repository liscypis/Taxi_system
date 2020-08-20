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
import { FormsModule } from '@angular/forms';
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
import {TableVirtualScrollModule} from 'ng-table-virtual-scroll';
import {CdkScrollableModule} from '@angular/cdk/scrolling';
import { ScrollingModule } from '@angular/cdk/scrolling';


@NgModule({
  declarations: [
    AppComponent,
    TollbarComponent,
    AdminComponent,
    LoginComponent,
    NewRideComponent,
    AcriveRidesComponent
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
    ScrollingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

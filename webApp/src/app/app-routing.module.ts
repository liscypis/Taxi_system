import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AdminComponent } from './admin/admin.component';
import { LoginComponent } from './login/login.component';
import { NewRideComponent } from './new-ride/new-ride.component';
import {AcriveRidesComponent} from './acrive-rides/acrive-rides.component'
import {RideHistoryComponent} from './ride-history/ride-history.component'


const routes: Routes = [
  {path: 'login', component:LoginComponent},
  {path: 'admin', component:AdminComponent},
  {path: 'newRide', component:NewRideComponent},
  {path: 'activeRides', component:AcriveRidesComponent},
  {path: 'rideHistory', component:RideHistoryComponent},
  
  {path: '', redirectTo: '/login', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

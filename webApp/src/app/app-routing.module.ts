import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AdminComponent } from './admin/admin.component';
import { LoginComponent } from './login/login.component';
import { NewRideComponent } from './new-ride/new-ride.component';


const routes: Routes = [
  {path: 'login', component:LoginComponent},
  {path: 'admin', component:AdminComponent},
  {path: 'newRide', component:NewRideComponent},
  {path: '', redirectTo: '/login', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

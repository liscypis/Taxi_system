import { Injectable } from '@angular/core';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const AUTH_API = 'http://localhost:8081/api/auth/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class AuthServiceService {

  constructor(private http: HttpClient) { }

  login(data): Observable<any> {
    console.log(data)
    return this.http.post(AUTH_API + 'signin', {
      userName: data.userName,
      password: data.password
    }, httpOptions);
  }


  // register(user): Observable<any> {
  //   return this.http.post(AUTH_API + 'signup', {
  //     username: user.username,
  //     email: user.email,
  //     password: user.password
  //   }, httpOptions);
  // }

}

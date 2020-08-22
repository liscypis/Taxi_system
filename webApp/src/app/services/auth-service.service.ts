import { Injectable } from '@angular/core';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RegisterRequest } from '../models/RegisterRequest';

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

  register(request: RegisterRequest): Observable<any> {
    console.log(request)
    return this.http.post(AUTH_API + 'signup', request);
  }


}

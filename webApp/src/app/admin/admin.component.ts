import { Component, OnInit } from '@angular/core';
import { TokenStorageService } from '../services/token-storage.service';
import { FormBuilder, Validators, FormGroup, FormControl } from '@angular/forms';
import { RegisterRequest } from '../models/RegisterRequest';
import { AuthServiceService } from '../services/auth-service.service';
import { NewCarRequest } from '../models/NewCarRequest';
import { UserDetails } from '../models/UserDetails';
import { APIService } from '../services/api.service';
import { CarInfo} from '../models/CarInfo'



@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {
  // addcar
  displayedColumns: string[] = ['id', 'name', 'surname', 'phoneNum'];
  dataSource: Array<UserDetails>;
  newCar = new NewCarRequest();

  driverSecelted = false;
  driverId: number;
  carAdded = false;

  request = new RegisterRequest();

  carForm = new FormGroup({
    mark: new FormControl('', [Validators.minLength(2), Validators.required]),
    model: new FormControl('', [Validators.minLength(1), Validators.required]),
    color: new FormControl('', [Validators.minLength(3), Validators.required]),
    registrationNumber: new FormControl('', [Validators.minLength(5), Validators.maxLength(7), Validators.required]),
  })


  isLoggedIn = false;
  
  // register
  phoneInUse = false;
  loginInUse = false;
  emailInUse = false;

  success = false;

  dataForm = new FormGroup({
    name: new FormControl('', [Validators.minLength(4), Validators.required]),
    surname: new FormControl('', [Validators.minLength(3), Validators.required]),
    login: new FormControl('', [Validators.minLength(4), Validators.required]),
    password: new FormControl('', [Validators.minLength(5), Validators.required]),
    email: new FormControl('', [Validators.minLength(6), this.emailValidator, Validators.required]),
    phone: new FormControl('', [Validators.minLength(9), Validators.maxLength(9), Validators.pattern("^[0-9]*$"), Validators.required]),
    role: new FormControl('', [Validators.required])
  })

  //edit car
  displayedCarColumns: string[] = ['id', 'carModel', 'carBrand', 'color', 'registrationNumber', 'edit', 'delete'];
  carDataSource: Array<CarInfo>;



  constructor(private tokenStorageService: TokenStorageService,
    private auth: AuthServiceService,
    private api: APIService) { }

  ngOnInit(): void {
    if (this.tokenStorageService.getJWTToken()) {
      this.isLoggedIn = true;
      this.getDrivers();
      this.getAllCars();
    }
  }

  onSubmit() {
    console.warn(this.dataForm);
    if (this.dataForm.value.role == "admin")
      this.request.roles = ["admin", "dispatcher"];
    else
      this.request.roles = [this.dataForm.value.role];

    this.request.surname = this.dataForm.value.surname;
    this.request.name = this.dataForm.value.name;
    this.request.userName = this.dataForm.value.login;
    this.request.password = this.dataForm.value.password;
    this.request.email = this.dataForm.value.email;
    this.request.phoneNum = this.dataForm.value.phone;

    console.log('request data:', this.request);

    this.resetHintWarn();
    this.register();
  }

  register(): void {
    this.auth.register(this.request).subscribe(
      data => {
        console.log(data)
        this.success = true;
      },
      err => {
        console.log(err.error.message)
        if (err.error.message == "Phone number is already in use")
          this.phoneInUse = true;
        if (err.error.message == "Username is already in use")
          this.loginInUse = true;
        if (err.error.message == "Email is already in use")
          this.emailInUse = true;
      }
    );
  }

  getDrivers(): void {
    this.api.getDriverWithoutCar().subscribe(
      data => {
        console.log(data);
        this.dataSource = data;
      }
    )
  }


  resetHintWarn(): void {
    this.loginInUse = false;
    this.emailInUse = false;
    this.phoneInUse = false;
  }



  emailValidator(control) {
    // RFC 2822 compliant regex
    if (
      control.value.match(
        /[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?/
      )
    ) {
      return null;
    } else {
      return { invalidEmailAddress: true };
    }
  }

  onNoCarRowClicked(row) {
    console.log('Row clicked: ', row);
    this.driverSecelted = true;
    this.driverId = row.id;
  }

  addCar(): void {
    this.newCar.registrationNumber = this.carForm.value.registrationNumber;
    this.newCar.color = this.carForm.value.color;
    this.newCar.carModel = this.carForm.value.model;
    this.newCar.carBrand = this.carForm.value.mark;
    this.newCar.driverID = this.driverId;

    console.log(this.newCar);
    this.api.addCar(this.newCar).subscribe(
      data => {
        console.log(data.msg);
        this.driverId = null;
        this.driverSecelted = false;
        this.carAdded = true;
        this.dataSource = null;
        this.getDrivers();

        this.getAllCars();
      },
      err => {
        console.log(err.error.message)
      }
    );
  }


  getAllCars(): void {
    this.api.getAllCars().subscribe(
      data => {
        console.log(data);
        this.carDataSource = data;
      },
      err => {
        console.log(err.error.message)
      }
    );
  }


}

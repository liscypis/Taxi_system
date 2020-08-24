import { Component, OnInit, ViewChild } from '@angular/core';
import { UpdateUser } from '../../models/UpdateUser';
import { APIService } from 'src/app/services/api.service';
import { FormGroup, FormControl, Validators, NgForm } from '@angular/forms';

interface Role {
  value: string;
  viewValue: string;
}

@Component({
  selector: 'app-edit-user',
  templateUrl: './edit-user.component.html',
  styleUrls: ['./edit-user.component.css']
})
export class EditUserComponent implements OnInit {
  roles: Role[] = [
    { value: 'user', viewValue: 'Użytkownik' },
    { value: 'driver', viewValue: 'Kierowca' },
    { value: 'dispatcher', viewValue: 'Dyspozytor' },
    { value: 'admin', viewValue: 'Admin' }
  ];

  updatedUser = new UpdateUser();
  displayedColumns: string[] = ['id', 'name', 'surname', 'userName', 'email', 'phoneNum', 'edit'];
  dataSource: Array<UpdateUser>;

  selectedRole: String;
  idUser: number;


  dataForm = new FormGroup({
    name: new FormControl('', [Validators.minLength(4), Validators.required]),
    surname: new FormControl('', [Validators.minLength(3), Validators.required]),
    login: new FormControl('', [Validators.minLength(4), Validators.required]),
    email: new FormControl('', [Validators.minLength(6), this.emailValidator, Validators.required]),
    phone: new FormControl('', [Validators.minLength(9), Validators.maxLength(9), Validators.pattern("^[0-9]*$"), Validators.required]),
    role: new FormControl('', [Validators.required])
  })

  phoneInUse: boolean;
  loginInUse: boolean;
  emailInUse: boolean;

  constructor(private api: APIService) { }

  ngOnInit(): void {
  }

  getUsersByRole(role): void {
    console.log(role)

    this.api.getUserByRole(role).subscribe(
      data => {
        console.log(data.msg);
        this.dataSource = data;
      },
      err => {
        console.log(err.error.message);
      }
    );
  }

  changeRole(value): void {
    console.log(value);
    this.selectedRole = value;
    this.getUsersByRole(value);
  }

  onDeleteRowClicked(row): void {
    this.api.deleteUser(Number(row.id)).subscribe(
      data => {
        console.log(data.msg);
        this.getUsersByRole(this.selectedRole);
      },
      err => {
        console.log(err.error.message);
      }
    );
  }
  onEditRowClicked(row): void {
    if(row.userName != "") {
      this.idUser = row.id;
      this.dataForm.setValue({
        name: row.name,
        surname: row.surname,
        login: row.userName,
        email: row.email,
        phone: row.phoneNum,
        role: this.selectedRole
      })
    }
   

  }

  saveUser(): void {
    this.resetErrors();

    this.updatedUser.id = this.idUser;
    this.updatedUser.name = this.dataForm.value.name;
    this.updatedUser.surname = this.dataForm.value.surname;
    this.updatedUser.userName = this.dataForm.value.login;
    this.updatedUser.email = this.dataForm.value.email;
    this.updatedUser.phoneNum = this.dataForm.value.phone;
    if (this.dataForm.value.role == "admin")
      this.updatedUser.roles = ["admin", "dispatcher"];
    else
      this.updatedUser.roles = [this.dataForm.value.role];

    this.api.updateUser(this.updatedUser).subscribe(
      data => {
        console.log(data.msg);
        this.getUsersByRole(this.selectedRole);
        this.resetErrors();

      },
      err => {
        console.log(err.error.message);
        if (err.error.message == "Phone in use")
          this.phoneInUse = true;
        if (err.error.message == "Login in use")
          this.loginInUse = true;
        if (err.error.message == "Email in use")
          this.emailInUse = true;
      }
    );
  }


  resetErrors(): void {
    this.phoneInUse = false;
    this.loginInUse = false;
    this.emailInUse = false;
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
}

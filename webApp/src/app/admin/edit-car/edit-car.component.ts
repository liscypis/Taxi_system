import { Component, OnInit, ViewChild } from '@angular/core';
import { CarInfo } from 'src/app/models/CarInfo';
import { APIService } from 'src/app/services/api.service';
import { FormGroup, FormControl, Validators, NgForm } from '@angular/forms';
import { collectExternalReferences } from '@angular/compiler';

@Component({
  selector: 'app-edit-car',
  templateUrl: './edit-car.component.html',
  styleUrls: ['./edit-car.component.css']
})
export class EditCarComponent implements OnInit {
  @ViewChild('formDirective') private formDirective: NgForm;

  displayedCarColumns: string[] = ['id', 'carModel', 'carBrand', 'color', 'registrationNumber', 'edit'];
  carDataSource: Array<CarInfo>;

  editedCar = new CarInfo();

  carForm = new FormGroup({
    mark: new FormControl('', [Validators.minLength(2), Validators.required]),
    model: new FormControl('', [Validators.minLength(1), Validators.required]),
    color: new FormControl('', [Validators.minLength(3), Validators.required]),
    registrationNumber: new FormControl('', [Validators.minLength(5), Validators.maxLength(7), Validators.required]),
  })
  carSelected: boolean;


  constructor(private api: APIService) { }

  ngOnInit(): void {
    this.carSelected = false;
    this.getAllCars();
    if (this.formDirective != null)
      this.formDirective.resetForm();
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

  editCar(): void {
    this.editedCar.carBrand = this.carForm.value.mark;
    this.editedCar.carModel = this.carForm.value.model;
    this.editedCar.color = this.carForm.value.color;
    this.editedCar.registrationNumber = this.carForm.value.registrationNumber;
    console.log(this.editCar);
    this.api.updateCar(this.editedCar).subscribe(
      data => {
        console.log(data.msg);
        this.carForm.reset();
        this.ngOnInit();
      },
      err => {
        console.log(err.error.message);
      }
    );
  }

  onEditRowClicked(row): void {
    this.editedCar.id = row.id;
    this.carForm.setValue({
      mark: row.carBrand,
      model: row.carModel,
      color: row.color,
      registrationNumber: row.registrationNumber
    })
  }

  onDeleteRowClicked(row): void {
    console.log(row)

    this.api.deleteCar(Number(row.id)).subscribe(
      data =>{
        console.log(data.msg);
        this.ngOnInit();
      },
      err =>{
        console.log(err.error.message);
      }
    );
  }

}

package com.lisowski.server.DTO;

import com.lisowski.server.models.Car;
import com.lisowski.server.models.User;
import lombok.Data;

@Data
public class CarDTO {
    private Long id;
    private String carModel;
    private String carBrand;
    private String color;
    private String registrationNumber;

    public CarDTO(User driver) {
        this.carModel = driver.getCar().getCarModel();
        this.carBrand = driver.getCar().getCarBrand();
        this.color = driver.getCar().getColor();
        this.registrationNumber = driver.getCar().getRegistrationNumber();
    }

    public CarDTO(Car car) {
        this.id = car.getId();
        this.carModel = car.getCarModel();
        this.carBrand = car.getCarBrand();
        this.color = car.getColor();
        this.registrationNumber = car.getRegistrationNumber();
    }
}

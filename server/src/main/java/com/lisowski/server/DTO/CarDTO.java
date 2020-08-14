package com.lisowski.server.DTO;

import com.lisowski.server.models.User;
import lombok.Data;

@Data
public class CarDTO {
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
}

package com.lisowski.server.DTO.response;

import com.lisowski.server.models.User;
import lombok.Data;


@Data
public class DriverInfoResponse {
    private String name;
    private String carModel;
    private String carBrand;
    private String color;
    private String registrationNumber;

    public DriverInfoResponse(User driver) {
        this.name = driver.getName();
        this.carModel = driver.getCar().getCarModel();
        this.carBrand = driver.getCar().getCarBrand();
        this.color = driver.getCar().getColor();
        this.registrationNumber = driver.getCar().getRegistrationNumber();
    }
}

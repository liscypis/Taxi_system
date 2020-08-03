package com.lisowski.server.DTO.request;

import lombok.Data;

@Data
public class AddCarRequest {
    private Long driverID;
    private String carModel;
    private String carBrand;
    private String color;
    private String registrationNumber;
}

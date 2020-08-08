package com.lisowski.server.DTO.request;

import lombok.Data;

@Data
public class ConfirmRide {
    private Long idRide;
    private Boolean confirm;
}

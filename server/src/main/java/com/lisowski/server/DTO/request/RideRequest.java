package com.lisowski.server.DTO.request;

import lombok.Data;

@Data
public class RideRequest {
    private Long userId;
    private String origin;
    private String destination;
}

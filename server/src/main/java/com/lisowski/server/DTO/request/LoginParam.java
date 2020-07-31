package com.lisowski.server.DTO.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginParam {
    @NotBlank
    private String userName;
    @NotBlank
    private String password;
}

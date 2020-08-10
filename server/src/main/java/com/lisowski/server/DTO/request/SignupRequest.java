package com.lisowski.server.DTO.request;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class SignupRequest {

    @Size(max = 20)
    private String name;

    @Size(max = 40)
    private String surname;

    @Size(max = 30)
    private String userName;

    @Size(min = 5, max = 20)
    private String password;

    @Size(max = 40)
    private String email;

    @Size(max = 9)
    private String phoneNum;

    private Set<String> roles;

}

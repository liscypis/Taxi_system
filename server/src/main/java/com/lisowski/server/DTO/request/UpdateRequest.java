package com.lisowski.server.DTO.request;

import lombok.Data;


import java.util.List;
@Data
public class UpdateRequest {
    private Long id;
    private String name;
    private String surname;
    private String userName;
    private String email;
    private String phoneNum;
    private List<String> roles;
}

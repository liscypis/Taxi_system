package com.lisowski.server.DTO.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lisowski.server.security.UserDetailsImpl;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class LoginResponse {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String name;
    private String surname;
    private String userName;
    private String email;
    private String phoneNum;
    private List<String> roles;

    public LoginResponse(String token, UserDetailsImpl user){
        this.token = token;
        this.id = user.getId();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.userName = user.getUsername();
        this.email = user.getEmail();
        this.phoneNum = user.getPhoneNum();
        this.roles = user.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
    }
}

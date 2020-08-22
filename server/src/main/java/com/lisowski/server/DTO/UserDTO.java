package com.lisowski.server.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lisowski.server.models.Car;
import com.lisowski.server.models.Role;
import com.lisowski.server.models.User;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String surname;
    private String userName;
    private String email;
    private String phoneNum;
    private Long carId;
    private String status;
    private List<String> roles;

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.userName = user.getUserName();
        this.email = user.getEmail();
        this.phoneNum = user.getPhoneNum();
        if(user.getStatus() != null)
            this.status = user.getStatus().getStatus().name();
        if(user.getCar() != null)
            this.carId = user.getCar().getId();
        this.roles = user.getRoles().stream()
                .map(item -> item.getRole().name())
                .collect(Collectors.toList());
    }

}

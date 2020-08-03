package com.lisowski.server.api;

import com.lisowski.server.DTO.UserDTO;
import com.lisowski.server.DTO.request.AddCarRequest;
import com.lisowski.server.DTO.request.LoginParam;
import com.lisowski.server.DTO.request.SignupRequest;
import com.lisowski.server.repository.RoleRepository;
import com.lisowski.server.repository.UserRepository;
import com.lisowski.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/usersbyrole/{role}")
    public ResponseEntity<List<UserDTO>> authenticateUser(@PathVariable("role") String role) {
        return userService.getUsersByRole(role);
    }
    @DeleteMapping("/deleteUser/{userID}")
    public ResponseEntity<?> editCar(@PathVariable("userID") Long id) {
        return userService.deleteUser(id);
    }

}

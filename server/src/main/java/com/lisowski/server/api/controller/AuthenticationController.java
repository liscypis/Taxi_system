package com.lisowski.server.api.controller;

import com.lisowski.server.DTO.request.LoginParam;
import com.lisowski.server.DTO.request.SignupRequest;
import com.lisowski.server.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/signip")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginParam loginParam) {
       return authenticationService.authenticateUser(loginParam);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest request) {

        return authenticationService.registerUser(request);
    }

}

package com.lisowski.server.api;

import com.lisowski.server.repository.RoleRepository;
import com.lisowski.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/s")
public class TestController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;


    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }



    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public String userAccess() {
        return "User Content.";
    }


    @GetMapping("/driver")
    @PreAuthorize("hasRole('DRIVER')")
    public String driverAccess() {
        return "User Content.";
    }

    @GetMapping("/mod")
    @PreAuthorize("hasRole('DISPATCHER')")
    public String moderatorAccess() {
        return "DISPATCHER Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }

}
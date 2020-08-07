package com.lisowski.server.api;

import com.lisowski.server.DTO.request.RideRequest;
import com.lisowski.server.repository.RoleRepository;
import com.lisowski.server.repository.UserRepository;
import com.lisowski.server.services.RideService;
import com.lisowski.server.services.map.GoogleMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    GoogleMapService googleMapService;

    @Autowired
    RideService rideService;

    //TODO wywalic


    @DeleteMapping("/deleteRide/{id}")
    public void delete(@PathVariable("id") Long id) {
        rideService.deleteRide(id);
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
package com.lisowski.server.api;

import com.lisowski.server.DTO.request.RideRequest;
import com.lisowski.server.services.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class RideController {

    @Autowired
    RideService rideService;

    @PostMapping("/initialOrderRide")
    public ResponseEntity<?> initialOrderRide(@Valid @RequestBody RideRequest request) {
        return rideService.createPreDetailsRide(request);
    }
}

package com.lisowski.server.api;

import com.lisowski.server.DTO.request.ConfirmRide;
import com.lisowski.server.DTO.request.RideRequest;
import com.lisowski.server.DTO.request.StatusMessage;
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
    @PostMapping("/confirmRide")
    public ResponseEntity<?> confirmRide(@Valid @RequestBody ConfirmRide request) {
        return rideService.confirmRide(request);
    }

    @PutMapping("/setRideStatus")
    public ResponseEntity<?> setRideStatus(@Valid @RequestBody StatusMessage request) {
        return rideService.setRideStatus(request);
    }

    @GetMapping("/getRideStatus/{id}")
    public ResponseEntity<?> getRideStatus(@PathVariable("id") Long id) {
        return rideService.getRideStatus(id);
    }

    @GetMapping("/checkForNewRide/{id}")
    public ResponseEntity<?> checkForNewRide(@PathVariable("id") Long id) {
        return rideService.checkForNewRide(id);
    }


    @PostMapping("/confirmDriverArrival")
    public ResponseEntity<?> confirmDriverArrival(@Valid @RequestBody ConfirmRide request) {
        return rideService.confirmDriverArrival(request);
    }

    @GetMapping("/getPriceForRide/{id_ride}")
    public ResponseEntity<?> getPriceForRide(@PathVariable("id_ride") Long id) {
        return rideService.getPriceForRide(id);
    }

}

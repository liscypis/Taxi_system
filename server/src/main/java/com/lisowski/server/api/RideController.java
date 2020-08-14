package com.lisowski.server.api;

import com.lisowski.server.DTO.RideDTO;
import com.lisowski.server.DTO.request.ConfirmRide;
import com.lisowski.server.DTO.request.RideRequest;
import com.lisowski.server.DTO.request.StatusMessage;
import com.lisowski.server.DTO.response.Message;
import com.lisowski.server.models.Ride;
import com.lisowski.server.services.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class RideController {

    @Autowired
    RideService rideService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/initialOrderRide")
    public ResponseEntity<?> initialOrderRide(@Valid @RequestBody RideRequest request) {
        return rideService.createPreDetailsRide(request);
    }

    @PostMapping("/initialOrderRideByDispatcher")
    public ResponseEntity<?> initialOrderRideByDispatcher(@Valid @RequestBody RideRequest request) {
        return rideService.createPreDetailsRideByDispatcher(request);
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
    public ResponseEntity<Message> getRideStatus(@PathVariable("id") Long id) {
        Ride ride = rideService.getRideStatus(id);
            return ResponseEntity.ok(new Message(ride.getRideStatus()));
    }

    @GetMapping("/checkForNewRide/{id}")
    public ResponseEntity<?> checkForNewRide(@PathVariable("id") Long id) {
        return rideService.checkForNewRide(id);
    }


    @PutMapping("/confirmDriverArrival/{id}")
    public ResponseEntity<Message> confirmDriverArrival(@PathVariable("id") Long id) {
        return ResponseEntity.ok(rideService.confirmDriverArrival(id));
    }

    @GetMapping("/getPriceForRide/{id_ride}")
    public ResponseEntity<?> getPriceForRide(@PathVariable("id_ride") Long id) {
        return rideService.getPriceForRide(id);
    }

    @GetMapping("/getDriverRidesByDriverId/{id_driver}")
    public ResponseEntity<List<RideDTO>> getDriverRidesById(@PathVariable("id_driver") Long id) {
        List<RideDTO> rideDTOList = rideService.getDriverRides(id);
        if(rideDTOList != null)
            return ResponseEntity.ok(rideDTOList);
        else
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rides not found");
    }

    @GetMapping("/getUserRidesByUserId/{id_user}")
    public ResponseEntity<List<RideDTO>> getUserRidesById(@PathVariable("id_user") Long id) {
        List<RideDTO> rideDTOList = rideService.getUserRides(id);
        if(rideDTOList != null)
            return ResponseEntity.ok(rideDTOList);
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rides not found");
    }


}

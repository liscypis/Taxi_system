package com.lisowski.server.api;

import com.lisowski.server.DTO.UserDTO;
import com.lisowski.server.DTO.request.AddCarRequest;
import com.lisowski.server.DTO.request.LocationLog;
import com.lisowski.server.DTO.request.StatusMessage;
import com.lisowski.server.models.Car;
import com.lisowski.server.services.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class DriverController {

    @Autowired
    DriverService driverService;


    @PostMapping("/addCar")
    public ResponseEntity<?> addCarToDriver(@Valid @RequestBody AddCarRequest request) {
        return driverService.addCarToDriver(request);
    }

    @PostMapping("/editCar")
    public ResponseEntity<?> editCar(@Valid @RequestBody Car request) {
        return driverService.updateCar(request);
    }

    @DeleteMapping("/deleteCar/{carID}")
    public ResponseEntity<?> editCar(@PathVariable("carID") Long id) {
        return driverService.deleteCar(id);
    }

    @PostMapping("/addLocation")
    public ResponseEntity<?> editCar(@Valid @RequestBody LocationLog request) {
        return driverService.addLocalization(request);
    }
    @PostMapping("/setStatus")
    public ResponseEntity<?> setStatus(@Valid @RequestBody StatusMessage message) {
        return driverService.setDriverStatus(message);
    }
    @GetMapping("/getDriverLocation/{driverID}")
    public String authenticateUser(@PathVariable("driverID") Long id) {
        return driverService.getDriverPosition(id);
    }


}

package com.lisowski.server.services;

import com.lisowski.server.DTO.request.AddCarRequest;
import com.lisowski.server.DTO.request.LocationLog;
import com.lisowski.server.DTO.request.StatusMessage;
import com.lisowski.server.models.*;
import com.lisowski.server.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.time.Instant;

@Service
public class DriverService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    CarRepository carRepository;
    @Autowired
    DriverPosHistRepository driverPosHistRepository;
    @Autowired
    StatusRepository statusRepository;

    public ResponseEntity<?> addCarToDriver(AddCarRequest request) {
        Optional<User> userOptional = userRepository.findById(request.getDriverID());
        if(userOptional.isPresent()){
            User user = userOptional.get();
            Car newCar = new Car();
            newCar.setCarBrand(request.getCarBrand());
            newCar.setCarModel(request.getCarModel());
            newCar.setColor(request.getColor());
            newCar.setRegistrationNumber(request.getRegistrationNumber());
            user.setCar(newCar);
            userRepository.save(user);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Users not found");
        }
        return ResponseEntity.ok("Car added successfully!");
    }

    public ResponseEntity<?> updateCar(Car request) {
        Optional<Car> userOptional = carRepository.findById(request.getId());
        if(userOptional.isPresent()){
            carRepository.save(request);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "car not found");
        }
        return ResponseEntity.ok("Car edited successfully!");
    }

    public ResponseEntity<?> deleteCar(Long id) {
        Optional<Car> carOptional = carRepository.findById(id);
        if(carOptional.isPresent()){
            Optional<User> userOptional = userRepository.findByCarId(id);
            if(userOptional.isPresent()){
                User user = userOptional.get();
                user.setCar(null);
                userRepository.save(user);
            }
            carRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "car not found");
        }
        return ResponseEntity.ok("Car deleted successfully!");
    }

    public ResponseEntity<?> addLocalization(LocationLog locationLog) {
        Optional<User> userOptional = userRepository.findById(locationLog.getDriverId());
        if(userOptional.isPresent()){
            DriverPositionHistory position = new DriverPositionHistory();
            position.setDriver(userOptional.get());
            position.setLocation(locationLog.getLocation());
            driverPosHistRepository.save(position);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Driver not found");
        }
        return ResponseEntity.ok("Location added successfully!");
    }

    public ResponseEntity<?> setDriverStatus(StatusMessage message) {
        Optional<Status> fStatus = checkStatusFromMessage(message);
        if(fStatus.isPresent()) {
            Optional<User> optDriver = userRepository.findById(message.getId());
            if(optDriver.isPresent()){
                User driver = optDriver.get();
                driver.setStatus(fStatus.get());
                userRepository.save(driver);
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Driver id not found");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Driver role not found");
        }
        return ResponseEntity.ok("Status has been set successfully!");
    }

    private Optional<Status> checkStatusFromMessage(StatusMessage message) {
        Optional<Status> fStatus = Optional.empty();
        if(message.getStatus().equals("offline"))
            fStatus = statusRepository.findByStatus(EStatus.STATUS_OFFLINE);
        if(message.getStatus().equals("available"))
            fStatus = statusRepository.findByStatus(EStatus.STATUS_AVAILABLE);
        if(message.getStatus().equals("busy"))
            fStatus = statusRepository.findByStatus(EStatus.STATUS_BUSY);
        return fStatus;
    }
}

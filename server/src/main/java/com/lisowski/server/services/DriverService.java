package com.lisowski.server.services;

import com.lisowski.server.DTO.CarDTO;
import com.lisowski.server.DTO.DriverPositionHistoryDTO;
import com.lisowski.server.DTO.RideDTO;
import com.lisowski.server.DTO.request.AddCarRequest;
import com.lisowski.server.DTO.request.LocationLog;
import com.lisowski.server.DTO.request.StatusMessage;
import com.lisowski.server.DTO.response.Message;
import com.lisowski.server.models.*;
import com.lisowski.server.models.enums.EStatus;
import com.lisowski.server.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            if(userOptional.get().getCar() == null) {
                User user = userOptional.get();
                Car newCar = new Car();
                newCar.setCarBrand(request.getCarBrand());
                newCar.setCarModel(request.getCarModel());
                newCar.setColor(request.getColor());
                newCar.setRegistrationNumber(request.getRegistrationNumber());
                user.setCar(newCar);
                userRepository.save(user);
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Driver already has a car.");

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Driver not found");
        }
        return ResponseEntity.ok(new Message("Car added successfully!"));
    }

    public ResponseEntity<?> updateCar(Car request) {
        Optional<Car> userOptional = carRepository.findById(request.getId());
        if(userOptional.isPresent()){
            carRepository.save(request);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "car not found");
        }
        return ResponseEntity.ok(new Message("Car edited successfully!"));
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
        return ResponseEntity.ok(new Message("Car deleted successfully!"));
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
        return ResponseEntity.ok(new Message("Location added successfully!"));
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Driver status not found");
        }
        return ResponseEntity.ok(new Message("Status has been set successfully!"));
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

    public String getDriverPosition(Long driverId) {
        return  driverPosHistRepository.findLastPositions(List.of(driverId)).get(0).getLocation();
    }
    public CarDTO getDriverCar(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));;
        return new CarDTO(user);
    }

    public List<CarDTO> getCars() {
        return carRepository.findAll().stream().map(CarDTO::new).collect(Collectors.toList());
    }
}

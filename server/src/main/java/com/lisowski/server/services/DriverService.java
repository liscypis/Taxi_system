package com.lisowski.server.services;

import com.lisowski.server.DTO.request.AddCarRequest;
import com.lisowski.server.models.Car;
import com.lisowski.server.models.User;
import com.lisowski.server.repository.CarRepository;
import com.lisowski.server.repository.RoleRepository;
import com.lisowski.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class DriverService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    CarRepository carRepository;

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
}

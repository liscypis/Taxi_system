package com.lisowski.server.services;

import com.lisowski.server.DTO.DriverPositionHistoryDTO;
import com.lisowski.server.DTO.request.RideRequest;
import com.lisowski.server.DTO.request.StatusMessage;
import com.lisowski.server.models.EStatus;
import com.lisowski.server.models.Status;
import com.lisowski.server.models.User;
import com.lisowski.server.repository.DriverPosHistRepository;
import com.lisowski.server.repository.UserRepository;
import com.lisowski.server.services.map.GoogleMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RideService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    DriverPosHistRepository driverPosHistRepository;
    @Autowired
    GoogleMapService googleMapService;


    //TODO add direction
    public ResponseEntity<?> createPreDetailsRide(RideRequest request) {
        Optional<User> optionalUser = userRepository.findById(request.getUserId());
        if(optionalUser.isPresent()) {
            Optional<List<Long>> ids = getIdsOfAvailableDrivers();
            if(ids.isPresent()){
                List<DriverPositionHistoryDTO> lastPositions = driverPosHistRepository.findLastPositions(ids.get());
                List<String> driversPosition = lastPositions.stream().map(DriverPositionHistoryDTO::getLocation).collect(Collectors.toList());
                String[] arrayOfPositions = driversPosition.toArray(String[]::new);
                Long index = googleMapService.findClosestDriver(request.getOrigin(),arrayOfPositions);
                System.out.println(lastPositions.get(index.intValue()));
                googleMapService.getDirection(lastPositions.get(index.intValue()).getLocation(),request.getDestination(),request.getOrigin());

            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No drivers, try again later");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }
        return ResponseEntity.ok("Status has been set successfully!");
    }
    private Optional<List<Long>> getIdsOfAvailableDrivers() {
        return userRepository.findIdsByStatus(EStatus.STATUS_AVAILABLE);
    }
}

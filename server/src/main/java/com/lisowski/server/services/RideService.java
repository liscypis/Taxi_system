package com.lisowski.server.services;

import com.lisowski.server.DTO.DriverPositionHistoryDTO;
import com.lisowski.server.DTO.request.RideRequest;
import com.lisowski.server.DTO.response.RideDetailsResponse;
import com.lisowski.server.models.Ride;
import com.lisowski.server.models.RideDetails;
import com.lisowski.server.models.Status;
import com.lisowski.server.models.enums.ERideStatus;
import com.lisowski.server.models.enums.EStatus;
import com.lisowski.server.models.User;
import com.lisowski.server.repository.*;
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
    @Autowired
    RideDetailsRepository rideDetailsRepository;
    @Autowired
    RideRepository rideRepository;
    @Autowired
    StatusRepository statusRepository;


    public ResponseEntity<?> createPreDetailsRide(RideRequest request) {
        Optional<User> optionalUser = userRepository.findById(request.getUserId());
        if(optionalUser.isPresent()) {
            Optional<List<Long>> ids = getIdsOfAvailableDrivers();
            if(ids.isPresent()){
                return ResponseEntity.ok(createInitialRideDetailsResponse(request, ids.get()));
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No drivers, try again later");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }
    }

    private RideDetailsResponse createInitialRideDetailsResponse(RideRequest request, List<Long> ids) {
        List<DriverPositionHistoryDTO> lastPositions = driverPosHistRepository.findLastPositions(ids);
        Long index = getClosestDriverIndex(request, lastPositions);
        RideDetailsResponse details = googleMapService.getRideInfo(lastPositions.get(index.intValue()).getLocation(), request.getDestination(), request.getOrigin());

        RideDetails savedRideDet = saveAndGetRideDetails(request, lastPositions, index, details);
        Ride savedRide = saveAndGetRide(request, lastPositions, index, savedRideDet);
        System.out.println(savedRide.getId());

        User driver = userRepository.findById(lastPositions.get(index.intValue()).getDriverId()).get();
//        Status status = statusRepository.findByStatus(EStatus.STATUS_INITIAL).get();
        driver.setStatus(statusRepository.findByStatus(EStatus.STATUS_INITIAL).get());
        userRepository.save(driver);

        details.setIdRide(savedRide.getId());
        System.out.println(details.toString());

        return details;
    }

    private Ride saveAndGetRide(RideRequest request, List<DriverPositionHistoryDTO> lastPositions, Long index, RideDetails savedRideDet) {
        Ride ride = new Ride();
        User driver = userRepository.findById(lastPositions.get(index.intValue()).getDriverId()).get();
        User user = userRepository.findById(request.getUserId()).get();
        ride.setDriver(driver);
        ride.setUser(user);
        ride.setRideStatus(ERideStatus.INITIAL.name());
        ride.setRideDetails(savedRideDet);
        return rideRepository.saveAndFlush(ride);
    }

    private RideDetails saveAndGetRideDetails(RideRequest request, List<DriverPositionHistoryDTO> lastPositions, Long index, RideDetailsResponse details) {
        RideDetails rideDetails = new RideDetails();
        rideDetails.setStartPoint(lastPositions.get(index.intValue()).getLocation());
        rideDetails.setEndPoint(request.getDestination());
        rideDetails.setDriverPolyline(details.getDriverPolyline());
        rideDetails.setUserPolyline(details.getUserPolyline());
        return rideDetailsRepository.saveAndFlush(rideDetails);
    }

    private Long getClosestDriverIndex(RideRequest request, List<DriverPositionHistoryDTO> lastPositions) {
        List<String> driversPosition = lastPositions.stream().map(DriverPositionHistoryDTO::getLocation).collect(Collectors.toList());
        String[] arrayOfPositions = driversPosition.toArray(String[]::new);
        return googleMapService.findClosestDriver(request.getOrigin(),arrayOfPositions);
    }

    private Optional<List<Long>> getIdsOfAvailableDrivers() {
        return userRepository.findIdsByStatus(EStatus.STATUS_AVAILABLE);
    }

    public void deleteRide(Long id){
        rideRepository.deleteById(id);
    }
}

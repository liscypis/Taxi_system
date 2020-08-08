package com.lisowski.server.services;

import com.lisowski.server.DTO.DriverPositionHistoryDTO;
import com.lisowski.server.DTO.request.ConfirmRide;
import com.lisowski.server.DTO.request.RideRequest;
import com.lisowski.server.DTO.request.StatusMessage;
import com.lisowski.server.DTO.response.DriverInfoResponse;
import com.lisowski.server.DTO.response.RideDetailsResponse;
import com.lisowski.server.models.Ride;
import com.lisowski.server.models.RideDetails;
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
        if (optionalUser.isPresent()) {
            Optional<List<Long>> ids = getIdsOfAvailableDrivers();
            if (ids.isPresent()) {
                return ResponseEntity.ok(createInitialRideDetailsResponse(request, ids.get()));
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No drivers, try again later");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }
    }

    public ResponseEntity<?> confirmRide(ConfirmRide request) {
        Optional<Ride> optRide = rideRepository.findById(request.getIdRide());
        if (optRide.isPresent()) {
            Ride ride = optRide.get();
            if (request.getConfirm()) {
                ride.setRideStatus(ERideStatus.ON_THE_WAY_TO_CLIENT.name());
                rideRepository.save(ride);
                setDriverStatus(ride.getDriver().getId(), EStatus.STATUS_BUSY);

                return ResponseEntity.ok(getDriverInformation(ride));
            } else {
                setDriverStatus(ride.getDriver().getId(), EStatus.STATUS_AVAILABLE);
                rideRepository.delete(ride);

                return ResponseEntity.ok("Taxi order canceled");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride not found");
        }
    }

    private DriverInfoResponse getDriverInformation(Ride ride) {
        User driver = userRepository.findById(ride.getDriver().getId()).get();
        return new DriverInfoResponse(driver);
    }

    private RideDetailsResponse createInitialRideDetailsResponse(RideRequest request, List<Long> ids) {
        List<DriverPositionHistoryDTO> lastPositions = driverPosHistRepository.findLastPositions(ids);
        Long index = getClosestDriverIndex(request, lastPositions);
        RideDetailsResponse details = googleMapService.getRideInfo(lastPositions.get(index.intValue()).getLocation(), request.getDestination(), request.getOrigin());

        RideDetails savedRideDet = saveAndGetRideDetails(request, lastPositions, index, details);
        Ride savedRide = saveAndGetRide(request, lastPositions, index, savedRideDet);
        System.out.println(savedRide.getId());

        setDriverStatus(lastPositions.get(index.intValue()).getDriverId(), EStatus.STATUS_INITIAL);

        details.setIdRide(savedRide.getId());
        details.setIdDriver(savedRide.getDriver().getId());
        details.setUserDestination(savedRideDet.getEndPoint());
        details.setUserLocation(savedRideDet.getWaypoint());
        System.out.println(details.toString());

        return details;
    }

    private void setDriverStatus(Long driverID, EStatus status) {
        User driver = userRepository.findById(driverID).get();
        driver.setStatus(statusRepository.findByStatus(status).get());
        userRepository.save(driver);
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
        rideDetails.setWaypoint(request.getOrigin());
        rideDetails.setEndPoint(request.getDestination());
        rideDetails.setDriverPolyline(details.getDriverPolyline());
        rideDetails.setUserPolyline(details.getUserPolyline());
        rideDetails.setUserDistance(details.getUserDistance());
        rideDetails.setDriverDistance(details.getDriverDistance());
        return rideDetailsRepository.saveAndFlush(rideDetails);
    }

    private Long getClosestDriverIndex(RideRequest request, List<DriverPositionHistoryDTO> lastPositions) {
        List<String> driversPosition = lastPositions.stream().map(DriverPositionHistoryDTO::getLocation).collect(Collectors.toList());
        String[] arrayOfPositions = driversPosition.toArray(String[]::new);
        return googleMapService.findClosestDriver(request.getOrigin(), arrayOfPositions);
    }

    private Optional<List<Long>> getIdsOfAvailableDrivers() {
        return userRepository.findIdsByStatus(EStatus.STATUS_AVAILABLE);
    }

    public void deleteRide(Long id) {
        rideRepository.deleteById(id);
    }

    public ResponseEntity<?> setRideStatusByDriver(StatusMessage request) {
        Optional<Ride> optRide = rideRepository.findById(request.getId());
        String status = checkStatusName(request);
        if (optRide.isPresent() && !status.equals("")) {
            Ride ride = optRide.get();
            ride.setRideStatus(status);
            rideRepository.save(ride);

            return ResponseEntity.ok("Status set successfully");
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride or status not found");
    }

    private String checkStatusName(StatusMessage request) {
        String status = "";
        if (request.getStatus().equals("wait_to_user"))
            status = ERideStatus.WAITING_FOR_USER.name();
        if (request.getStatus().equals("way_to_destination"))
            status = ERideStatus.ON_THE_WAY_TO_DEST.name();
        if (request.getStatus().equals("ending"))
            status = ERideStatus.ENDING.name();
        if (request.getStatus().equals("complete"))
            status = ERideStatus.COMPLETE.name();

        return status;
    }

    public ResponseEntity<?> getRideStatus(Long id) {
        Ride ride = rideRepository.findById(id).orElseThrow(() ->new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride or status not found"));
        return ResponseEntity.ok(ride.getRideStatus());
    }

    public ResponseEntity<?> checkForNewRide(Long id) {
        Optional<Ride> optRide = rideRepository.findByDriver_IdAndRideStatus(id, ERideStatus.ON_THE_WAY_TO_CLIENT.name());
        if(optRide.isPresent()){
            Ride ride = optRide.get();
            return ResponseEntity.ok(new RideDetailsResponse(ride));
        }
        else {
            return ResponseEntity.ok(new RideDetailsResponse());
        }
    }
}

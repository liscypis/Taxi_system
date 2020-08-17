package com.lisowski.server.services;

import com.lisowski.server.DTO.DriverPositionHistoryDTO;
import com.lisowski.server.DTO.RideDTO;
import com.lisowski.server.DTO.request.ConfirmRide;
import com.lisowski.server.DTO.request.RideRating;
import com.lisowski.server.DTO.request.RideRequest;
import com.lisowski.server.DTO.request.StatusMessage;
import com.lisowski.server.DTO.response.DriverInfoResponse;
import com.lisowski.server.DTO.response.Message;
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

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.lisowski.server.Utils.Price.calculatePrice;

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
    @Autowired
    AuthenticationService authenticationService;


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

    public ResponseEntity<?> createPreDetailsRideByDispatcher(RideRequest request) {
        Optional<User> optionalUser = userRepository.findByPhoneNum(request.getPhoneNumber());
        Optional<List<Long>> ids = getIdsOfAvailableDrivers();
        if (ids.isPresent()) {
            if (optionalUser.isPresent()) {
                request.setUserId(optionalUser.get().getId());
            } else {
                User user = new User();
                user.setUserName("");
                user.setPassword("");
                user.setName("");
                user.setSurname("");
                user.setEmail("");
                user.setPhoneNum(request.getPhoneNumber());
                user.setRoles(authenticationService.checkRoles(Set.of("user")));
                User savedUser = userRepository.saveAndFlush(user);
                request.setUserId(savedUser.getId());
            }
            return ResponseEntity.ok(createInitialRideDetailsResponse(request, ids.get()));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No drivers, try again later");
        }
    }

    public ResponseEntity<?> confirmRide(ConfirmRide request) {
        Optional<Ride> optRide = rideRepository.findById(request.getIdRide());
        if (optRide.isPresent()) {
            Ride ride = optRide.get();
            if (request.getConfirm()) {
                if(request.getNoApp())
                    ride.setRideStatus(ERideStatus.NO_APP.name());
                else
                    ride.setRideStatus(ERideStatus.ON_THE_WAY_TO_CLIENT.name());

                ride.getRideDetails().setTimeStart(Instant.now());
                rideRepository.save(ride);
                setDriverStatus(ride.getDriver().getId(), EStatus.STATUS_BUSY);

                return ResponseEntity.ok(getDriverInformation(ride));
            } else {
                setDriverStatus(ride.getDriver().getId(), EStatus.STATUS_AVAILABLE);
                rideRepository.delete(ride);

                return ResponseEntity.ok(new Message("Taxi order canceled"));
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

        RideDetails savedRideDet = saveAndGetRideDetails(lastPositions, index, details);
        Ride savedRide = saveAndGetRide(request, lastPositions, index, savedRideDet);
//        System.out.println(savedRide.getId());

        setDriverStatus(lastPositions.get(index.intValue()).getDriverId(), EStatus.STATUS_INITIAL);

        details.setIdRide(savedRide.getId());
        details.setIdDriver(savedRide.getDriver().getId());
        details.setUserPhone(savedRide.getUser().getPhoneNum());
        details.setDriverPhone(savedRide.getDriver().getPhoneNum());
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

    private RideDetails saveAndGetRideDetails(List<DriverPositionHistoryDTO> lastPositions, Long index, RideDetailsResponse details) {
        RideDetails rideDetails = new RideDetails();
        rideDetails.setStartPoint(lastPositions.get(index.intValue()).getLocation());
        rideDetails.setWaypoint(details.getUserLocation());
        rideDetails.setEndPoint(details.getUserDestination());
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

    public ResponseEntity<?> setRideStatus(StatusMessage request) {
        Optional<Ride> optRide = rideRepository.findById(request.getId());
        String status = checkStatusName(request);
        if (optRide.isPresent() && !status.equals("")) {
            Ride ride = optRide.get();
            ride.setRideStatus(status);
            rideRepository.save(ride);

            return ResponseEntity.ok(new Message("Status set successfully"));
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

    public Ride getRideStatus(Long id) {
        return rideRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride or status not found"));
    }

    public ResponseEntity<?> checkForNewRide(Long id) {
        Optional<Ride> optRide = rideRepository.findByDriver_IdAndRideStatusOrRideStatus(id, ERideStatus.ON_THE_WAY_TO_CLIENT.name(),ERideStatus.NO_APP.name());
        if (optRide.isPresent()) {
            Ride ride = optRide.get();
            return ResponseEntity.ok(new RideDetailsResponse(ride));
        } else {
            return ResponseEntity.ok(new RideDetailsResponse());
        }
    }

    public Message confirmDriverArrival(Long id) {
        Ride ride = rideRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride not found"));
        ride.setRideStatus(ERideStatus.ON_THE_WAY_TO_DEST.name());
        ride.getRideDetails().setTimeArriveToUser(Instant.now());
        rideRepository.save(ride);

        return new Message("Status successfully set");
    }

    public float getPriceForRide(Long id) {
        Ride ride = rideRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride not found"));
        ride.getRideDetails().setTimeEnd(Instant.now());
        Duration duration = Duration.between(ride.getRideDetails().getTimeStart(), ride.getRideDetails().getTimeEnd());
        ride.getRideDetails().setPrice(calculatePrice(duration.getSeconds(), ride.getRideDetails().getUserDistance()));
        rideRepository.save(ride);

        return ride.getRideDetails().getPrice();
    }

    public List<RideDTO> getDriverRides(Long driverId) {
        Optional<List<Ride>> listOfRides = rideRepository.findByDriver_IdAndRideStatus(driverId, ERideStatus.COMPLETE.name());
        return listOfRides.map(rides -> rides.stream().map(RideDTO::new).collect(Collectors.toList())).orElse(null);
    }

    public List<RideDTO> getUserRides(Long userId) {
        Optional<List<Ride>> listOfRides = rideRepository.findByUser_IdAndRideStatus(userId, ERideStatus.COMPLETE.name());
        return listOfRides.map(rides -> rides.stream().map(RideDTO::new).collect(Collectors.toList())).orElse(null);
    }

    public Message setRideRate(RideRating request) {
        Ride ride = rideRepository.findById(request.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride not found"));
        ride.getRideDetails().setRating(request.getRate());
        rideRepository.save(ride);
        return new Message("Rating saved successfully.");
    }
}

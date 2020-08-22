package com.lisowski.server.api;

import com.lisowski.server.DTO.UserDTO;
import com.lisowski.server.DTO.DriverPositionHistoryDTO;
import com.lisowski.server.DTO.request.UpdateRequest;
import com.lisowski.server.DTO.response.Message;
import com.lisowski.server.models.DriverPositionHistory;
import com.lisowski.server.models.User;
import com.lisowski.server.repository.DriverPosHistRepository;
import com.lisowski.server.repository.UserRepository;
import com.lisowski.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    DriverPosHistRepository driverPosHistRepository;

    @GetMapping("/usersbyrole/{role}")
    public ResponseEntity<List<UserDTO>> getUsers(@PathVariable("role") String role) {
        return userService.getUsersByRole(role);
    }
    @DeleteMapping("/deleteUser/{userID}")
    public ResponseEntity<?> editCar(@PathVariable("userID") Long id) {
        return userService.deleteUser(id);
    }

    @GetMapping("/driverWithoutCar")
    public ResponseEntity<List<UserDTO>> getDriversWithoutCar() {
        List<UserDTO> drivers = userService.getDriverWithoutCar();
        if(drivers != null)
            return ResponseEntity.ok(drivers);
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No drivers");
    }

    @PutMapping("/updateUser")
    public ResponseEntity<Message> updateUser(@Valid @RequestBody UpdateRequest request) {
        userService.updateUser(request);
            return ResponseEntity.ok(new Message("User successfully edited"));
    }


    //TODO DELETE THIS
    @GetMapping("/testLastLocc")
    public List<DriverPositionHistoryDTO> locc() {
        return driverPosHistRepository.findLastPositions(List.of(2L,4L));
    }
}

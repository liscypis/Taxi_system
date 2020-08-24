package com.lisowski.server.services;

import com.lisowski.server.DTO.RideDTO;
import com.lisowski.server.DTO.UserDTO;
import com.lisowski.server.DTO.request.UpdateRequest;
import com.lisowski.server.DTO.response.Message;
import com.lisowski.server.models.enums.ERole;
import com.lisowski.server.models.Role;
import com.lisowski.server.models.User;
import com.lisowski.server.repository.DriverPosHistRepository;
import com.lisowski.server.repository.RideRepository;
import com.lisowski.server.repository.RoleRepository;
import com.lisowski.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    RideRepository rideRepository;
    @Autowired
    DriverPosHistRepository histRepository;

    public ResponseEntity<List<UserDTO>> getUsersByRole(String role) {
        Optional<Role> fRole = Optional.empty();
        if (role.equals("user"))
            fRole = roleRepository.findByRole(ERole.ROLE_USER);
        if (role.equals("driver"))
            fRole = roleRepository.findByRole(ERole.ROLE_DRIVER);
        if (role.equals("dispatcher"))
            fRole = roleRepository.findByRole(ERole.ROLE_DISPATCHER);
        if (role.equals("admin"))
            fRole = roleRepository.findByRole(ERole.ROLE_ADMIN);

        if (fRole.isPresent()) {
            Set<Role> roleDB = Set.of(fRole.get());
            Optional<List<User>> listOfUsers = userRepository.findByRolesIn(roleDB);
            if (listOfUsers.isPresent()) {
                List<UserDTO> foundUsers = listOfUsers.get().stream().map(usr -> new UserDTO(usr)).collect(Collectors.toList());
                return ResponseEntity.ok(foundUsers);
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Users not found");
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role");
    }

    //TODO poprawic
    @Transactional
    public ResponseEntity<?> deleteUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User u = userOptional.get();
            rideRepository.deleteAllByUser(u);
            rideRepository.deleteAllByDriver(u);
            rideRepository.flush();
            histRepository.deleteAllByDriver(u);
            histRepository.flush();
            userRepository.delete(u);
//            userRepository.deleteById(u.getId());
//            userRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }
        return ResponseEntity.ok(new Message("User deleted successfully!"));
    }


    public List<UserDTO> getDriverWithoutCar() {
        Role roleDriver = roleRepository.findByRole(ERole.ROLE_DRIVER).get();
        Optional<List<User>> driver = userRepository.findByCarAndRolesIn(null, Set.of(roleDriver));
        return driver.map(dri -> dri.stream().map(UserDTO::new).collect(Collectors.toList())).orElse(null);
    }

    public void updateUser(UpdateRequest request) {
        User emailInUse = userRepository.findByEmail(request.getEmail()).orElse(null);
        User phoneInUse = userRepository.findByPhoneNum(request.getPhoneNum()).orElse(null);
        User loginInUse = userRepository.findByUserName(request.getUserName()).orElse(null);
        if (emailInUse != null && !emailInUse.getId().equals(request.getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email in use");
        if (phoneInUse != null && !phoneInUse.getId().equals(request.getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone in use");
        if (loginInUse != null && !loginInUse.getId().equals(request.getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Login in use");

        Set<Role> roles = authenticationService.checkRoles(new HashSet<>(request.getRoles()));
        User edited = userRepository.findById(request.getId()).get();
        edited.setPhoneNum(request.getPhoneNum());
        edited.setRoles(roles);
        edited.setName(request.getName());
        edited.setSurname(request.getSurname());
        edited.setUserName(request.getUserName());
        edited.setEmail(request.getEmail());
        userRepository.save(edited);
    }
}

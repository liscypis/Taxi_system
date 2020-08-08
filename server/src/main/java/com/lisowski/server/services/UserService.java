package com.lisowski.server.services;

import com.lisowski.server.DTO.UserDTO;
import com.lisowski.server.models.enums.ERole;
import com.lisowski.server.models.Role;
import com.lisowski.server.models.User;
import com.lisowski.server.repository.RoleRepository;
import com.lisowski.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public ResponseEntity<List<UserDTO>> getUsersByRole(String role) {
        Optional<Role> fRole = Optional.empty();
        if(role.equals("user"))
            fRole = roleRepository.findByRole(ERole.ROLE_USER);
        if(role.equals("driver"))
            fRole = roleRepository.findByRole(ERole.ROLE_DRIVER);
        if(role.equals("dispatcher"))
            fRole = roleRepository.findByRole(ERole.ROLE_DISPATCHER);
        if(role.equals("admin"))
            fRole = roleRepository.findByRole(ERole.ROLE_ADMIN);

        if(fRole.isPresent()){
            Set<Role> roleDB = Set.of(fRole.get());
            Optional<List<User>> listOfUsers = userRepository.findByRolesIn(roleDB);
            if(listOfUsers.isPresent()){
                List<UserDTO> foundUsers = listOfUsers.get().stream().map(usr -> new UserDTO(usr)).collect(Collectors.toList());
                return ResponseEntity.ok(foundUsers);
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Users not found");
        }
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role");
    }


    public ResponseEntity<?> deleteUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()){
            userRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }
        return ResponseEntity.ok("User deleted successfully!");
    }


}

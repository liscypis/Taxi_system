package com.lisowski.server.services;

import com.lisowski.server.DTO.request.LoginParam;
import com.lisowski.server.DTO.request.SignupRequest;
import com.lisowski.server.DTO.response.LoginResponse;
import com.lisowski.server.Utils.AuthUtils;
import com.lisowski.server.models.ERole;
import com.lisowski.server.models.Role;
import com.lisowski.server.models.User;
import com.lisowski.server.repository.RoleRepository;
import com.lisowski.server.repository.UserRepository;
import com.lisowski.server.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    AuthUtils authUtils;


    public ResponseEntity<?> authenticateUser(LoginParam loginParam) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginParam.getUserName(), loginParam.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = authUtils.generateJWTToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(new LoginResponse(jwt, userDetails));
    }

    public ResponseEntity<?> registerUser(SignupRequest signupRequest) {
        Optional<User> user = userRepository.findByPhoneNum(signupRequest.getPhoneNum());
        if (user.isPresent()) {
            if (!user.get().getUserName().equals("")){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone number is already in use");
            } else {
                checkEmailAndUserName(signupRequest);

                User modifiedUser = user.get();
                modifiedUser.setName(signupRequest.getName());
                modifiedUser.setSurname(signupRequest.getSurname());
                modifiedUser.setUserName(signupRequest.getUserName());
                modifiedUser.setPassword(encoder.encode(signupRequest.getPassword()));
                modifiedUser.setEmail(signupRequest.getEmail());
                modifiedUser.setPhoneNum(signupRequest.getPhoneNum());
                userRepository.save(modifiedUser);

                return ResponseEntity.ok("User created successfully!");
            }
        } else {
            checkEmailAndUserName(signupRequest);

            Set<String> roles = signupRequest.getRoles();
            User newUser = new User();

            newUser.setUserName(signupRequest.getUserName());
            newUser.setPassword(encoder.encode(signupRequest.getPassword()));
            newUser.setName(signupRequest.getName());
            newUser.setSurname(signupRequest.getSurname());
            newUser.setEmail(signupRequest.getEmail());
            newUser.setPhoneNum(signupRequest.getPhoneNum());
            newUser.setRoles(checkRoles(roles));

            userRepository.save(newUser);
        }


        return ResponseEntity.ok("User created successfully!");
    }

    private void checkEmailAndUserName(SignupRequest signupRequest) {
        if(!signupRequest.getUserName().equals("") || !signupRequest.getEmail().equals("")) {
            if (userRepository.existsByUserName(signupRequest.getUserName()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already in use");
            if (userRepository.existsByEmail(signupRequest.getEmail()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already in use");
        }
    }

    private Set<Role> checkRoles(Set<String> roles) {
        Set<Role> newRoles = new HashSet<>();
        if (roles == null) {
            Role uRole = roleRepository.findByRole(ERole.ROLE_USER).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found"));
            newRoles.add(uRole);
        } else {
            roles.forEach(role -> {
                Role uRole = null;
                switch (role) {
                    case "admin":
                        uRole = roleRepository.findByRole(ERole.ROLE_ADMIN).orElseThrow(() ->
                                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found"));
                        newRoles.add(uRole);
                        break;
                    case "dispatcher":
                        uRole = roleRepository.findByRole(ERole.ROLE_DISPATCHER).orElseThrow(() ->
                                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found"));
                        newRoles.add(uRole);
                        break;
                    case "driver":
                        uRole = roleRepository.findByRole(ERole.ROLE_DRIVER).orElseThrow(() ->
                                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found"));
                        newRoles.add(uRole);
                        break;
                    case "user":
                        uRole = roleRepository.findByRole(ERole.ROLE_USER).orElseThrow(() ->
                                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found"));
                        newRoles.add(uRole);
                        break;
                }
            });
        }
        return newRoles;
    }
}

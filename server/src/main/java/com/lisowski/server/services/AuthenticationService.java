package com.lisowski.server.services;

import com.lisowski.server.DTO.request.LoginParam;
import com.lisowski.server.DTO.request.SignupRequest;
import com.lisowski.server.DTO.response.LoginResponse;
import com.lisowski.server.Utils.AuthUtils;
import com.lisowski.server.repository.RoleRepository;
import com.lisowski.server.repository.UserRepository;
import com.lisowski.server.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new LoginResponse(jwt, userDetails));
    }

    public ResponseEntity<?> registerUser(SignupRequest signupRequest){

        return ResponseEntity.ok(null);
    }
}

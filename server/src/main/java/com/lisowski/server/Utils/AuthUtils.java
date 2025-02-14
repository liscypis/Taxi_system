package com.lisowski.server.Utils;

import com.lisowski.server.security.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.SignatureException;
import java.util.Date;

@Component
public class AuthUtils {

    private String jwtSecret = "lisSecretKey";

    private int jwtExpirationMS = 86400000;

    public String generateJWTToken(Authentication authentication) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(principal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMS))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
    public boolean validateJwtToken(String token) {
        try{
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (UnsupportedJwtException | MalformedJwtException | ExpiredJwtException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public String getUserNameFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }
}

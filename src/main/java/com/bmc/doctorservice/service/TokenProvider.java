package com.bmc.doctorservice.service;

import com.bmc.doctorservice.model.UserPrincipal;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TokenProvider {

    private static final Logger log = LoggerFactory.getLogger(TokenProvider.class);

    private final String JWT_SECRET ="some_secret_code";

    public boolean validateToken(String jwt) {
        try {
             Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(jwt);
             return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;


    }
    public UserPrincipal getUserNameFromToken(String token){

        String userName =   Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token).getBody().getSubject();
        ArrayList<String> roles = (ArrayList<String>) Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token).getBody().get("Roles");
        List<GrantedAuthority> authorities = new ArrayList<>();
        for(String role: roles){
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return UserPrincipal
            .builder()
            .username(userName)
            .authorities(authorities)
            .build();
    }
}

package aem.example.springboot.graphqlbaseapp.infrastructure.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TokenProvider {
    private static final String SECRET = "secret";
    private static final String ISSUER = "graphql-core";
    private static final String AUTHORITIES_KEY = "roles";
    private static final Algorithm ALGORITHM = Algorithm.HMAC512(SECRET);

    public String generateToken(String username, String[] roles) {

        Instant now = Instant.now();
        Instant plus = now.plus(1, ChronoUnit.HOURS);

        return JWT.create()
                .withSubject(username)
                .withArrayClaim(AUTHORITIES_KEY, roles)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(plus))
                .withIssuer(ISSUER)
                .withClaim("type", "accessToken")
                .sign(ALGORITHM);
    }


    public String generateRefreshToken(String username) {

        Instant now = Instant.now();
        Instant plus = now.plus(1, ChronoUnit.DAYS);

        return JWT.create()
                .withSubject(username)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(plus))
                .withClaim("type", "refreshToken")
                .withIssuer(ISSUER)
                .sign(ALGORITHM);
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, "accessToken");
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, "refreshToken");
    }

    private boolean validateToken(String token, String claimType) {
        try {
            JWTVerifier verifier = JWT.require(ALGORITHM).withIssuer(ISSUER)
                    .withClaim("type", claimType)
                    .build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        DecodedJWT decode = JWT.decode(token);
        String username = decode.getSubject();
        String[] roles = decode.getClaim(AUTHORITIES_KEY).asArray(String.class);
        Set<SimpleGrantedAuthority> authorities = Arrays.stream(roles).map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
        User principal = new User(username, "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public String generateToken(Authentication authentication) {

        return generateToken(authentication.getName(), authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(String::toUpperCase).distinct().toArray(String[]::new));
    }
    
    public String getSubject(String token){
    	return JWT.decode(token).getSubject();
    }
}

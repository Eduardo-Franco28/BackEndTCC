package com.iment.app_mobile_tcc.shared.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.iment.app_mobile_tcc.users.entity.User;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class TokenService {

    public static final String TEO_SCOPE = "teo";

    private static final Duration TEO_TOKEN_TIME = Duration.ofHours(1);

    private String secret;

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("auth-integramente")
                    .withSubject(user.getEmail())
                    .withExpiresAt(this.getExpirationToken())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error at create token", exception);
        }
    }

    public String generateTeoToken(User user, Instant pinAt) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("auth-integramente")
                    .withSubject(user.getEmail())
                    .withClaim("scope", TEO_SCOPE)
                    .withClaim("pinAt", pinAt)
                    .withExpiresAt(Instant.now().plus(TEO_TOKEN_TIME))
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error at create token", exception);
        }
    }

    public String validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-integramente")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception){
            return "";
        }
    }

    public String getScope(String token){
        DecodedJWT decoded = this.decodeToken(token);

        if(decoded == null) return null;

        return decoded.getClaim("scope").asString();
    }

    public Instant getPinAt(String token){
        DecodedJWT decoded = this.decodeToken(token);

        if(decoded == null) return null;

        return decoded.getClaim("pinAt").asInstant();
    }

    private DecodedJWT decodeToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-integramente")
                    .build()
                    .verify(token);
        } catch (JWTVerificationException exception){
            return null;
        }
    }

    private Instant getExpirationToken() {
        return LocalDateTime.now().plusDays(60).toInstant(ZoneOffset.of("-03:00"));
    }
}

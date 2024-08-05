package io.github.stackpan.tasque.http.resource;

import org.springframework.security.oauth2.jwt.Jwt;

public record AuthResource(String token, String type, String expiresAt) {

    public static AuthResource fromJwt(Jwt jwt) {
        return new AuthResource(jwt.getTokenValue(), "JWT", jwt.getClaimAsString("exp"));
    }

}

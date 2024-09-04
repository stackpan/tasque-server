package io.github.stackpan.tasque.http.resource;

import org.springframework.security.oauth2.jwt.Jwt;

public record AuthResource(String token) {

    public static AuthResource fromToken(String serialized) {
        return new AuthResource(serialized);
    }

}

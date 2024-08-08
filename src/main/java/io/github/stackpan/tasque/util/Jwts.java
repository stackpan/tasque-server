package io.github.stackpan.tasque.util;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class Jwts {

    public static String getSubject(JwtAuthenticationToken token) {
        return (String) token.getTokenAttributes().get("sub");
    }
}

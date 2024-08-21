package io.github.stackpan.tasque.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthToken {

    public UUID getCurrentSubject() {
        var jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        var jwtSubject = (String) jwtAuthenticationToken.getTokenAttributes().get("sub");

        return UUID.fromString(jwtSubject);
    }
}

package io.github.stackpan.tasque;

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

public class UserMocks {
    public static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor rizkyJwt() {
        return jwt().jwt(jwt -> jwt
                .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                .claim("scope", "ROLE_USER")
        );
    }

    public static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor susanJwt() {
        return jwt().jwt(jwt -> jwt
                .claim("sub", "06eb9790-6375-4dcc-a46b-1e217e879174")
                .claim("scope", "ROLE_USER")
        );
    }
}

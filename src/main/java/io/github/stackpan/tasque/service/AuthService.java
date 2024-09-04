package io.github.stackpan.tasque.service;

import io.github.stackpan.tasque.data.AuthLoginDto;
import org.springframework.security.oauth2.jwt.Jwt;

public interface AuthService {

    String login(AuthLoginDto data);

}

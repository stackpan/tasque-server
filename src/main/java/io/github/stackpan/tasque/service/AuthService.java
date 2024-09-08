package io.github.stackpan.tasque.service;

import io.github.stackpan.tasque.data.AuthLoginDto;

public interface AuthService {

    String login(AuthLoginDto data);

}

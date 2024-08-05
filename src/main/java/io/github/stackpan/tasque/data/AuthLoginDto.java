package io.github.stackpan.tasque.data;

import io.github.stackpan.tasque.http.request.LoginRequest;

public record AuthLoginDto(String principal, String credential) {

    public static AuthLoginDto fromRequest(LoginRequest request) {
        return new AuthLoginDto(request.identity(), request.secret());
    }
}

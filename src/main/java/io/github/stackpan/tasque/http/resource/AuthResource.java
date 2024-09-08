package io.github.stackpan.tasque.http.resource;

public record AuthResource(String token) {

    public static AuthResource fromToken(String serialized) {
        return new AuthResource(serialized);
    }

}

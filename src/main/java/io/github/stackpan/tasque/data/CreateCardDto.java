package io.github.stackpan.tasque.data;

import io.github.stackpan.tasque.http.request.CreateCardRequest;

import java.util.Optional;

public record CreateCardDto(String body, Optional<String> colorHex) {
    public static CreateCardDto fromRequest(CreateCardRequest request) {
        return new CreateCardDto(request.body(), request.colorHex());
    }
}

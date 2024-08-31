package io.github.stackpan.tasque.data;

import io.github.stackpan.tasque.http.request.UpdateCardRequest;

import java.util.Optional;

public record UpdateCardDto(String body, Optional<String> colorHex) {
    public static UpdateCardDto fromRequest(UpdateCardRequest request) {
        return new UpdateCardDto(request.body(), request.colorHex());
    }
}

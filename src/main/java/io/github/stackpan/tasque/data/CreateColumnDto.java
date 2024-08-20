package io.github.stackpan.tasque.data;

import io.github.stackpan.tasque.http.request.CreateColumnRequest;

import java.util.Optional;
import java.util.UUID;

public record CreateColumnDto(
        String name,
        Optional<String> description,
        Optional<String> colorHex,
        Optional<Long> position
) {
    public static CreateColumnDto fromRequest(CreateColumnRequest request) {
        return new CreateColumnDto(
                request.name(),
                request.description(),
                request.colorHex(),
                request.position()
        );
    }
}

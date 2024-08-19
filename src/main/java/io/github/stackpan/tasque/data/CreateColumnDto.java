package io.github.stackpan.tasque.data;

import io.github.stackpan.tasque.http.request.CreateColumnRequest;

import java.util.Optional;
import java.util.UUID;

public record CreateColumnDto(
        String name,
        Optional<String> description,
        Optional<String> colorHex,
        Optional<UUID> nextColumnId
) {
    public static CreateColumnDto fromRequest(CreateColumnRequest request) {
        return new CreateColumnDto(
                request.name(),
                request.description(),
                request.colorHex(),
                request.nextColumnId().map(UUID::fromString)
        );
    }
}

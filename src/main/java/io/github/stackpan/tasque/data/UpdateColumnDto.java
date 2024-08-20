package io.github.stackpan.tasque.data;

import io.github.stackpan.tasque.http.request.UpdateColumnRequest;

public record UpdateColumnDto(
        String name,
        String description,
        String colorHex,
        Long position
) {
    public static UpdateColumnDto fromRequest(UpdateColumnRequest request) {
        return new UpdateColumnDto(
                request.name(),
                request.description(),
                request.colorHex(),
                request.position()
        );
    }
}

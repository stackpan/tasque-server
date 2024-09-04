package io.github.stackpan.tasque.data;

import io.github.stackpan.tasque.http.request.MoveCardRequest;

import java.util.UUID;

public record MoveCardDto(UUID targetCardId, UUID destinationColumnId) {
    public static MoveCardDto fromRequest(MoveCardRequest request) {
        return new MoveCardDto(
                UUID.fromString(request.targetCardId()),
                UUID.fromString(request.destinationColumnId())
        );
    }
}

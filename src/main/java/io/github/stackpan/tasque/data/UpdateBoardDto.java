package io.github.stackpan.tasque.data;

import io.github.stackpan.tasque.http.request.UpdateBoardRequest;

import java.util.Optional;

public record UpdateBoardDto(String name, Optional<String> description, Optional<String> colorHex) {

    public static UpdateBoardDto fromRequest(UpdateBoardRequest request) {
        return new UpdateBoardDto(request.name(), request.description(), request.colorHex());
    }
}

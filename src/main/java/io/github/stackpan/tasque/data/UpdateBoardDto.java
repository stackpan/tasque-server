package io.github.stackpan.tasque.data;

import io.github.stackpan.tasque.http.request.UpdateBoardRequest;

public record UpdateBoardDto(String name, String description, String colorHex) {

    public static UpdateBoardDto fromRequest(UpdateBoardRequest request) {
        return new UpdateBoardDto(request.name(), request.description(), request.colorHex());
    }
}

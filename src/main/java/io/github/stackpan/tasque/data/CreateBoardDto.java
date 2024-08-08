package io.github.stackpan.tasque.data;

import io.github.stackpan.tasque.http.request.CreateBoardRequest;

public record CreateBoardDto(String name, String description, String colorHex) {

    public static CreateBoardDto fromRequest(CreateBoardRequest request) {
        return new CreateBoardDto(request.name(), request.description(), request.colorHex());
    }
}

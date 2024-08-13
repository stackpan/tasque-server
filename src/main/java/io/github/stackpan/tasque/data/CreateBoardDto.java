package io.github.stackpan.tasque.data;

import io.github.stackpan.tasque.http.request.CreateBoardRequest;

import java.util.Optional;

public record CreateBoardDto(String name, Optional<String> description, Optional<String> colorHex) {

    public static CreateBoardDto fromRequest(CreateBoardRequest request) {
        return new CreateBoardDto(request.name(), request.description(), request.colorHex());
    }
}

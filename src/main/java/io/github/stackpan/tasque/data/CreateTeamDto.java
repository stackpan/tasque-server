package io.github.stackpan.tasque.data;

import io.github.stackpan.tasque.http.request.CreateTeamRequest;

import java.util.Optional;

public record CreateTeamDto(String name, Optional<String> description) {

    public static CreateTeamDto fromRequest(CreateTeamRequest request) {
        return new CreateTeamDto(request.name(), request.description());
    }
}

package io.github.stackpan.tasque.data;

import io.github.stackpan.tasque.http.request.UpdateTeamRequest;

import java.util.Optional;

public record UpdateTeamDto(String name, Optional<String> description) {

    public static UpdateTeamDto fromRequest(UpdateTeamRequest request) {
        return new UpdateTeamDto(
                request.name(),
                request.description()
        );
    }
}

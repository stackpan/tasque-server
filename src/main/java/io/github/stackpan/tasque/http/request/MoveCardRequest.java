package io.github.stackpan.tasque.http.request;

import io.github.stackpan.tasque.util.Regexps;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record MoveCardRequest(
        @NotNull @Pattern(regexp = Regexps.UUID, message = "{validation.constraints.Pattern.ValidUuid.message}") String targetCardId,
        @NotNull @Pattern(regexp = Regexps.UUID, message = "{validation.constraints.Pattern.ValidUuid.message}") String destinationColumnId
) {
}

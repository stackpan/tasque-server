package io.github.stackpan.tasque.http.request;

import io.github.stackpan.tasque.util.Regexps;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Optional;

public record CreateCardRequest(
        @NotNull @Size(max = 1024) @Pattern(regexp = Regexps.LIMITED_STRING, message = "{validation.constraints.Pattern.ValidString.message}") String body,
        @NotNull Optional<@Pattern(regexp = Regexps.COLOR_HEX, message = "{validation.constraints.Pattern.ColorHex.message}") String> colorHex
) {
}

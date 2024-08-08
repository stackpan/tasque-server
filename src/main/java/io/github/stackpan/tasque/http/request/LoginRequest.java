package io.github.stackpan.tasque.http.request;

import io.github.stackpan.tasque.util.Regexps;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotNull
        @Size(max = 50)
        @Pattern(regexp = Regexps.USER_IDENTITY, message = "{validation.constraints.Pattern.ValidString.message}")
        String identity,

        @NotNull
        @Size(max = 32)
        String secret
) {
}

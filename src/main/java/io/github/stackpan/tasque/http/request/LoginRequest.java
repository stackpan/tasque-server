package io.github.stackpan.tasque.http.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotNull @Size(max = 50) String identity,
        @NotNull @Size(max = 32) String secret
) {
}

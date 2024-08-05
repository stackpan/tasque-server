package io.github.stackpan.tasque.http.request;

import io.github.stackpan.tasque.validation.StringOnly;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotNull @StringOnly @Size(max = 50) String identity,
        @NotNull @StringOnly @Size(max = 32) String secret
) {
}

package io.github.stackpan.tasque.http.request;

import io.github.stackpan.tasque.validation.StringOnly;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateBoardRequest(
        @NotNull @StringOnly @Size(max = 64) String name,
        @StringOnly @Size(max = 1024) String description,
        @NotNull @Pattern(regexp = "^#[0-9a-f]{6}$", message = "must be valid color hex code") String colorHex
) {
}

package io.github.stackpan.tasque.http.request;

import io.github.stackpan.tasque.http.validation.annotations.ValidImage;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record UploadMeRequest(@NotNull @ValidImage MultipartFile profilePicture) {
}

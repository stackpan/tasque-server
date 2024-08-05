package io.github.stackpan.tasque.http.resource;

import io.github.stackpan.tasque.entity.User;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.hateoas.RepresentationModel;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Value
public class UserResource extends RepresentationModel<UserResource> {

    UUID id;

    String username;

    String email;

    String firstName;

    String lastName;

    String profilePictureUrl;

    OffsetDateTime emailVerifiedAt;

    OffsetDateTime createdAt;

    OffsetDateTime updatedAt;

    public static UserResource fromEntity(User user) {
        return new UserResource(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getProfilePictureUrl(),
                Objects.nonNull(user.getEmailVerifiedAt()) ? user.getEmailVerifiedAt().atOffset(ZoneOffset.UTC) : null,
                user.getCreatedAt().atOffset(ZoneOffset.UTC),
                user.getUpdatedAt().atOffset(ZoneOffset.UTC)
        );
    }
}

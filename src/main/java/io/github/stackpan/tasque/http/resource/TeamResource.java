package io.github.stackpan.tasque.http.resource;

import io.github.stackpan.tasque.entity.Team;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Value
public class TeamResource extends RepresentationModel<TeamResource> {

    UUID id;

    String name;

    String description;

    String profilePictureUrl;

    Instant createdAt;

    Instant updatedAt;

    public static TeamResource fromEntity(Team entity) {
        return new TeamResource(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getProfilePictureUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

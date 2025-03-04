package io.github.stackpan.tasque.http.resource;

import io.github.stackpan.tasque.entity.Team;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Value
@Relation(collectionRelation = "teams", itemRelation = "team")
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
                entity.getProfilePicture(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

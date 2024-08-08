package io.github.stackpan.tasque.http.resource;

import io.github.stackpan.tasque.entity.Board;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Value
@Relation(itemRelation = "board", collectionRelation = "boards")
public class BoardResource extends RepresentationModel<BoardResource> {

    UUID id;

    String name;

    String description;

    String bannerPictureUrl;

    String colorHex;

    UUID ownerId;

    String ownerType;

    OffsetDateTime createdAt;

    OffsetDateTime updatedAt;

    public static BoardResource fromEntity(Board entity) {
        return new BoardResource(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getBannerPictureUrl(),
                entity.getColorHex(),
                entity.getOwnerId(),
                entity.getOwnerType().toString(),
                entity.getCreatedAt().atOffset(ZoneOffset.UTC),
                entity.getUpdatedAt().atOffset(ZoneOffset.UTC)
        );
    }

}

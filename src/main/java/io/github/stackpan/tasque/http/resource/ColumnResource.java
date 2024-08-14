package io.github.stackpan.tasque.http.resource;

import io.github.stackpan.tasque.entity.BoardColumn;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@EqualsAndHashCode(callSuper=false)
@Value
@Relation(collectionRelation = "columns", itemRelation = "column")
public class ColumnResource extends RepresentationModel<ColumnResource> {

    UUID id;

    String name;

    String description;

    String colorHex;

    UUID nextColumnId;

    OffsetDateTime createdAt;

    OffsetDateTime updatedAt;

    public static ColumnResource fromEntity(BoardColumn entity) {
        return new ColumnResource(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getColorHex(),
                entity.getNextColumnId(),
                entity.getCreatedAt().atOffset(ZoneOffset.UTC),
                entity.getUpdatedAt().atOffset(ZoneOffset.UTC)
        );
    }
}

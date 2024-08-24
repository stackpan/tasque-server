package io.github.stackpan.tasque.http.resource;

import io.github.stackpan.tasque.entity.Card;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Value
@Relation(collectionRelation = "cards", itemRelation = "card")
public class CardResource extends RepresentationModel<CardResource> {

    UUID id;

    String body;

    String colorHex;

    OffsetDateTime createdAt;

    OffsetDateTime updatedAt;

    public static CardResource fromEntity(Card card) {
        return new CardResource(
                card.getId(),
                card.getBody(),
                card.getColorHex(),
                card.getCreatedAt().atOffset(ZoneOffset.UTC),
                card.getUpdatedAt().atOffset(ZoneOffset.UTC)
        );
    }
}

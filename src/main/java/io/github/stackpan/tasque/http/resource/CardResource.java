package io.github.stackpan.tasque.http.resource;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@EqualsAndHashCode(callSuper = true)
@Value
@Relation(collectionRelation = "cards", itemRelation = "card")
public class CardResource extends RepresentationModel<CardResource> {

}

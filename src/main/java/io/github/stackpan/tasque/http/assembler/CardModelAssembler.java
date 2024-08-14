package io.github.stackpan.tasque.http.assembler;

import io.github.stackpan.tasque.entity.Card;
import io.github.stackpan.tasque.http.resource.CardResource;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

public class CardModelAssembler implements RepresentationModelAssembler<Card, RepresentationModel<CardResource>> {
    @Override
    public RepresentationModel<CardResource> toModel(Card entity) {
        return null;
    }
}

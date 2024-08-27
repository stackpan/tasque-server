package io.github.stackpan.tasque.http.assembler;

import io.github.stackpan.tasque.entity.Card;
import io.github.stackpan.tasque.http.controller.BoardController;
import io.github.stackpan.tasque.http.controller.CardController;
import io.github.stackpan.tasque.http.controller.ColumnController;
import io.github.stackpan.tasque.http.resource.CardResource;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class CardModelAssembler implements RepresentationModelAssembler<Card, RepresentationModel<CardResource>> {
    @Override
    public RepresentationModel<CardResource> toModel(Card entity) {
        return HalModelBuilder.halModelOf(CardResource.fromEntity(entity))
                .link(linkTo(methodOn(BoardController.class).listBoards()).withRel("boards"))
                .link(linkTo(methodOn(BoardController.class).getBoard(entity.getColumn().getBoard().getId())).withRel("board"))
                .link(linkTo(methodOn(ColumnController.class).listColumns(entity.getColumn().getBoard().getId())).withRel("columns"))
                .link(linkTo(methodOn(ColumnController.class).getColumn(entity.getColumn().getBoard().getId(), entity.getColumn().getId())).withRel("column"))
                .link(linkTo(methodOn(CardController.class).listCards(entity.getColumn().getBoard().getId(), entity.getColumn().getId())).withRel("cards"))
                .link(linkTo(methodOn(CardController.class).getCard(entity.getColumn().getBoard().getId(), entity.getColumn().getId(), entity.getId())).withSelfRel())
                .build();
    }
}

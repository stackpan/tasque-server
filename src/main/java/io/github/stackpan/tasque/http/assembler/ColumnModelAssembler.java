package io.github.stackpan.tasque.http.assembler;

import io.github.stackpan.tasque.entity.BoardColumn;
import io.github.stackpan.tasque.http.controller.BoardController;
import io.github.stackpan.tasque.http.controller.ColumnController;
import io.github.stackpan.tasque.http.resource.ColumnResource;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ColumnModelAssembler implements RepresentationModelAssembler<BoardColumn, RepresentationModel<ColumnResource>> {
    @Override
    public RepresentationModel<ColumnResource> toModel(BoardColumn entity) {
        return HalModelBuilder.halModelOf(ColumnResource.fromEntity(entity))
                .embed(entity.getCards().stream().map(card -> new CardModelAssembler().toModel(card)).toList(), LinkRelation.of("cards"))
                .link(linkTo(methodOn(BoardController.class).listBoards(null)).withRel("boards"))
                .link(linkTo(methodOn(BoardController.class).getBoard(entity.getBoard().getId(), null)).withRel("board"))
                .link(linkTo(methodOn(ColumnController.class).listColumns(entity.getBoard().getId(), null)).withRel("columns"))
                .link(linkTo(methodOn(ColumnController.class).getColumn(entity.getBoard().getId(), entity.getId(), null)).withSelfRel())
                .build();
    }
}

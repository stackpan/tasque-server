package io.github.stackpan.tasque.http.assembler;

import io.github.stackpan.tasque.entity.Board;
import io.github.stackpan.tasque.entity.Team;
import io.github.stackpan.tasque.entity.User;
import io.github.stackpan.tasque.http.controller.BoardController;
import io.github.stackpan.tasque.http.resource.BoardResource;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@Component
public class BoardModelAssembler implements RepresentationModelAssembler<Board, RepresentationModel<BoardResource>> {

    @Override
    public RepresentationModel<BoardResource> toModel(Board entity) {
        var boardResource = BoardResource.fromEntity(entity);

        EntityModel<?> boardOwnerModel = switch (entity.getOwnerType()) {
            case USER -> new UserModelAssembler().toModel((User) entity.getOwner());
            case TEAM -> new TeamModelAssembler().toModel((Team) entity.getOwner());
        };

        return HalModelBuilder.halModelOf(boardResource)
                .embed(boardOwnerModel, LinkRelation.of("owner"))
                .link(linkTo(methodOn(BoardController.class).listBoards(null)).withRel("boards"))
                .link(linkTo(methodOn(BoardController.class).getBoard(entity.getId().toString(), null)).withSelfRel())
                .build();
    }
}

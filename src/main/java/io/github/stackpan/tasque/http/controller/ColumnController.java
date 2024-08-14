package io.github.stackpan.tasque.http.controller;

import io.github.stackpan.tasque.http.assembler.ColumnModelAssembler;
import io.github.stackpan.tasque.http.resource.ColumnResource;
import io.github.stackpan.tasque.service.ColumnService;
import io.github.stackpan.tasque.util.Jwts;
import io.github.stackpan.tasque.util.UUIDs;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/boards/{boardId}/columns")
@RequiredArgsConstructor
public class ColumnController {

    private final ColumnService columnService;

    @GetMapping
    public CollectionModel<RepresentationModel<ColumnResource>> listColumns(@PathVariable String boardId, JwtAuthenticationToken token) {
        var subject = Jwts.getSubject(token);
        var columns = columnService.listByBoardId(UUIDs.fromString(boardId), UUID.fromString(subject))
                .stream()
                .map(column -> new ColumnModelAssembler().toModel(column))
                .toList();

        return CollectionModel.of(
                columns,
                linkTo(methodOn(BoardController.class).listBoards(null)).withRel("boards"),
                linkTo(methodOn(BoardController.class).getBoard(boardId, null)).withRel("board"),
                linkTo(methodOn(ColumnController.class).listColumns(boardId, null)).withSelfRel()
        );
    }

    @GetMapping("/{columnId}")
    public RepresentationModel<ColumnResource> getColumn(@PathVariable String boardId, @PathVariable String columnId, JwtAuthenticationToken token) {
        var subject = Jwts.getSubject(token);
        var column = columnService.getByBoardIdAndId(
                UUIDs.fromString(boardId),
                UUIDs.fromString(columnId),
                UUID.fromString(subject)
        );

        return new ColumnModelAssembler().toModel(column);
    }
}

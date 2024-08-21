package io.github.stackpan.tasque.http.controller;

import io.github.stackpan.tasque.data.CreateColumnDto;
import io.github.stackpan.tasque.data.UpdateColumnDto;
import io.github.stackpan.tasque.http.assembler.ColumnModelAssembler;
import io.github.stackpan.tasque.http.request.CreateColumnRequest;
import io.github.stackpan.tasque.http.request.UpdateColumnRequest;
import io.github.stackpan.tasque.http.resource.ColumnResource;
import io.github.stackpan.tasque.service.ColumnService;
import io.github.stackpan.tasque.util.Jwts;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/boards/{boardId}/columns")
@RequiredArgsConstructor
public class ColumnController {

    private final ColumnService columnService;

    @GetMapping
    public CollectionModel<RepresentationModel<ColumnResource>> listColumns(@PathVariable UUID boardId, JwtAuthenticationToken token) {
        var subject = Jwts.getSubject(token);
        var columns = columnService.listByBoardId(boardId, UUID.fromString(subject))
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

    @PostMapping
    public ResponseEntity<RepresentationModel<ColumnResource>> getColumn(@PathVariable UUID boardId, @RequestBody @Valid CreateColumnRequest payload, JwtAuthenticationToken token) {
        var subject = Jwts.getSubject(token);
        var createdColumn = columnService.createByBoardId(boardId, CreateColumnDto.fromRequest(payload), UUID.fromString(subject));

        var model = new ColumnModelAssembler().toModel(createdColumn);
        return ResponseEntity.created(model.getRequiredLink("self").toUri()).body(model);
    }

    @GetMapping("/{columnId}")
    public RepresentationModel<ColumnResource> getColumn(@PathVariable UUID boardId, @PathVariable UUID columnId, JwtAuthenticationToken token) {
        var subject = Jwts.getSubject(token);
        var column = columnService.getByBoardIdAndId(boardId, columnId, UUID.fromString(subject));

        return new ColumnModelAssembler().toModel(column);
    }

    @PutMapping("/{columnId}")
    public RepresentationModel<ColumnResource> updateColumn(@PathVariable UUID boardId, @PathVariable UUID columnId, @RequestBody @Valid UpdateColumnRequest payload, JwtAuthenticationToken token) {
        var subject = Jwts.getSubject(token);
        var column = columnService.updateByBoardIdAndId(boardId, columnId, UpdateColumnDto.fromRequest(payload), UUID.fromString(subject));

        return new ColumnModelAssembler().toModel(column);
    }

    @DeleteMapping("/{columnId}")
    public ResponseEntity<Void> deleteColumn(@PathVariable UUID boardId, @PathVariable UUID columnId, JwtAuthenticationToken token) {
        var subject = Jwts.getSubject(token);
        columnService.deleteByBoardIdAndId(boardId, columnId, UUID.fromString(subject));

        return ResponseEntity.noContent().build();
    }
}

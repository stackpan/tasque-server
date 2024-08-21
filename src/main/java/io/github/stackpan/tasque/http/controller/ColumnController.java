package io.github.stackpan.tasque.http.controller;

import io.github.stackpan.tasque.data.CreateColumnDto;
import io.github.stackpan.tasque.data.UpdateColumnDto;
import io.github.stackpan.tasque.http.assembler.ColumnModelAssembler;
import io.github.stackpan.tasque.http.request.CreateColumnRequest;
import io.github.stackpan.tasque.http.request.UpdateColumnRequest;
import io.github.stackpan.tasque.http.resource.ColumnResource;
import io.github.stackpan.tasque.service.ColumnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
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
    public CollectionModel<RepresentationModel<ColumnResource>> listColumns(@PathVariable UUID boardId) {
        var columns = columnService.listByBoardId(boardId)
                .stream()
                .map(column -> new ColumnModelAssembler().toModel(column))
                .toList();

        return CollectionModel.of(
                columns,
                linkTo(methodOn(BoardController.class).listBoards()).withRel("boards"),
                linkTo(methodOn(BoardController.class).getBoard(boardId)).withRel("board"),
                linkTo(methodOn(ColumnController.class).listColumns(boardId)).withSelfRel()
        );
    }

    @PostMapping
    public ResponseEntity<RepresentationModel<ColumnResource>> getColumn(@PathVariable UUID boardId, @RequestBody @Valid CreateColumnRequest payload) {
        var dto = CreateColumnDto.fromRequest(payload);
        var createdColumn = columnService.createByBoardId(boardId, dto);

        var model = new ColumnModelAssembler().toModel(createdColumn);
        return ResponseEntity.created(model.getRequiredLink("self").toUri()).body(model);
    }

    @GetMapping("/{columnId}")
    public RepresentationModel<ColumnResource> getColumn(@PathVariable UUID boardId, @PathVariable UUID columnId) {
        var column = columnService.getByBoardIdAndId(boardId, columnId);

        return new ColumnModelAssembler().toModel(column);
    }

    @PutMapping("/{columnId}")
    public RepresentationModel<ColumnResource> updateColumn(@PathVariable UUID boardId, @PathVariable UUID columnId, @RequestBody @Valid UpdateColumnRequest payload) {
        var dto = UpdateColumnDto.fromRequest(payload);
        var column = columnService.updateByBoardIdAndId(boardId, columnId, dto);

        return new ColumnModelAssembler().toModel(column);
    }

    @DeleteMapping("/{columnId}")
    public ResponseEntity<Void> deleteColumn(@PathVariable UUID boardId, @PathVariable UUID columnId) {
        columnService.deleteByBoardIdAndId(boardId, columnId);

        return ResponseEntity.noContent().build();
    }
}

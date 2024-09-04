package io.github.stackpan.tasque.http.controller;

import io.github.stackpan.tasque.data.CreateBoardDto;
import io.github.stackpan.tasque.data.MoveCardDto;
import io.github.stackpan.tasque.data.UpdateBoardDto;
import io.github.stackpan.tasque.http.assembler.BoardModelAssembler;
import io.github.stackpan.tasque.http.assembler.ColumnModelAssembler;
import io.github.stackpan.tasque.http.request.CreateBoardRequest;
import io.github.stackpan.tasque.http.request.MoveCardRequest;
import io.github.stackpan.tasque.http.request.UpdateBoardRequest;
import io.github.stackpan.tasque.http.resource.BoardResource;
import io.github.stackpan.tasque.http.resource.ColumnResource;
import io.github.stackpan.tasque.service.BoardService;
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
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public CollectionModel<RepresentationModel<BoardResource>> listBoards() {
        var boards = boardService.list()
                .stream()
                .map(board -> new BoardModelAssembler().toModel(board))
                .toList();

        return CollectionModel.of(
                boards,
                linkTo(methodOn(BoardController.class).listBoards()).withSelfRel()
        );
    }

    @PostMapping
    public ResponseEntity<RepresentationModel<BoardResource>> createBoard(@RequestBody @Valid CreateBoardRequest board) {
        var createdBoard = boardService.create(CreateBoardDto.fromRequest(board));

        var model = new BoardModelAssembler().toModel(createdBoard);
        return ResponseEntity.created(model.getRequiredLink("self").toUri()).body(model);
    }

    @GetMapping("/{boardId}")
    public RepresentationModel<BoardResource> getBoard(@PathVariable UUID boardId) {
        var board = boardService.getById(boardId);

        return new BoardModelAssembler().toModel(board);
    }

    @PutMapping("/{boardId}")
    public RepresentationModel<BoardResource> updateBoard(@PathVariable UUID boardId, @RequestBody @Valid UpdateBoardRequest board) {
        var updatedBoard = boardService.updateById(boardId, UpdateBoardDto.fromRequest(board));

        return new BoardModelAssembler().toModel(updatedBoard);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable UUID boardId) {
        boardService.deleteById(boardId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{boardId}/move-card")
    public CollectionModel<RepresentationModel<ColumnResource>> moveCard(@PathVariable UUID boardId, @RequestBody @Valid MoveCardRequest request) {
        var updatedColumns = boardService.moveCard(boardId, MoveCardDto.fromRequest(request))
                .stream()
                .map(column -> new ColumnModelAssembler().toModel(column))
                .toList();

        return CollectionModel.of(
                updatedColumns,
                linkTo(methodOn(ColumnController.class).listColumns(boardId)).withSelfRel()
        );
    }
}

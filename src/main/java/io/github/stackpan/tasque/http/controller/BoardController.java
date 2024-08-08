package io.github.stackpan.tasque.http.controller;

import io.github.stackpan.tasque.data.CreateBoardDto;
import io.github.stackpan.tasque.data.UpdateBoardDto;
import io.github.stackpan.tasque.http.assembler.BoardModelAssembler;
import io.github.stackpan.tasque.http.request.CreateBoardRequest;
import io.github.stackpan.tasque.http.request.UpdateBoardRequest;
import io.github.stackpan.tasque.http.resource.BoardResource;
import io.github.stackpan.tasque.service.BoardService;
import io.github.stackpan.tasque.util.UUIDs;
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
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public CollectionModel<RepresentationModel<BoardResource>> listBoards(JwtAuthenticationToken token) {
        var subject = (String) token.getTokenAttributes().get("sub");
        var boards = boardService.listAsUser(UUID.fromString(subject))
                .stream()
                .map(board -> new BoardModelAssembler().toModel(board))
                .toList();

        return CollectionModel.of(
                boards,
                linkTo(methodOn(BoardController.class).listBoards(null)).withSelfRel()
        );
    }

    @PostMapping
    public ResponseEntity<RepresentationModel<BoardResource>> createBoard(@RequestBody @Valid CreateBoardRequest board, JwtAuthenticationToken token) {
        var subject = (String) token.getTokenAttributes().get("sub");
        var createdBoard = boardService.createAsUser(CreateBoardDto.fromRequest(board), UUID.fromString(subject));

        var model = new BoardModelAssembler().toModel(createdBoard);
        return ResponseEntity.created(model.getRequiredLink("self").toUri()).body(model);
    }

    @GetMapping("/{boardId}")
    public RepresentationModel<BoardResource> getBoard(@PathVariable String boardId, JwtAuthenticationToken token) {
        var subject = (String) token.getTokenAttributes().get("sub");
        var board = boardService.getAsUser(UUIDs.fromString(boardId), UUID.fromString(subject));

        return new BoardModelAssembler().toModel(board);
    }

    @PutMapping("/{boardId}")
    public RepresentationModel<BoardResource> updateBoard(@PathVariable String boardId, @RequestBody @Valid UpdateBoardRequest board, JwtAuthenticationToken token) {
        var subject = (String) token.getTokenAttributes().get("sub");
        var updatedBoard = boardService.updateByIdAsUser(UUIDs.fromString(boardId), UpdateBoardDto.fromRequest(board), UUID.fromString(subject));

        return new BoardModelAssembler().toModel(updatedBoard);
    }
}

package io.github.stackpan.tasque.http.controller;

import io.github.stackpan.tasque.http.assembler.BoardModelAssembler;
import io.github.stackpan.tasque.http.resource.BoardResource;
import io.github.stackpan.tasque.service.BoardService;
import io.github.stackpan.tasque.util.UUIDs;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        var boards = boardService.listByUserId(UUIDs.fromString(subject))
                .stream()
                .map(board -> new BoardModelAssembler().toModel(board))
                .toList();

        return CollectionModel.of(
                boards,
                linkTo(methodOn(BoardController.class).listBoards(null)).withSelfRel()
        );
    }

    @GetMapping("/{boardId}")
    public RepresentationModel<BoardResource> getBoard(@PathVariable String boardId, JwtAuthenticationToken token) {
        var board = boardService.getById(UUIDs.fromString(boardId));
        return new BoardModelAssembler().toModel(board);
    }

}

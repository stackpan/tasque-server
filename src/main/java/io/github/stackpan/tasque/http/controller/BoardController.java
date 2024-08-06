package io.github.stackpan.tasque.http.controller;

import io.github.stackpan.tasque.http.assembler.BoardModelAssembler;
import io.github.stackpan.tasque.http.resource.BoardResource;
import io.github.stackpan.tasque.service.BoardService;
import io.github.stackpan.tasque.util.UUIDs;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/{boardId}")
    public RepresentationModel<BoardResource> getBoard(@PathVariable String boardId, JwtAuthenticationToken token) {
        var board = boardService.getById(UUIDs.fromString(boardId));
        return new BoardModelAssembler().toModel(board);
    }

}

package io.github.stackpan.tasque.http.controller;

import io.github.stackpan.tasque.http.assembler.CardModelAssembler;
import io.github.stackpan.tasque.http.resource.CardResource;
import io.github.stackpan.tasque.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/boards/{boardId}/columns/{columnId}/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping("/{cardId}")
    public RepresentationModel<CardResource> getCard(@PathVariable UUID boardId, @PathVariable UUID columnId, @PathVariable UUID cardId) {
        var card = cardService.getByBoardIdAndColumnIdAndId(boardId, columnId, cardId);

        return new CardModelAssembler().toModel(card);
    }
}

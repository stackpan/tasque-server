package io.github.stackpan.tasque.http.controller;

import io.github.stackpan.tasque.data.CreateCardDto;
import io.github.stackpan.tasque.data.UpdateCardDto;
import io.github.stackpan.tasque.http.assembler.CardModelAssembler;
import io.github.stackpan.tasque.http.request.CreateCardRequest;
import io.github.stackpan.tasque.http.request.UpdateCardRequest;
import io.github.stackpan.tasque.http.resource.CardResource;
import io.github.stackpan.tasque.service.CardService;
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
@RequestMapping("/api/boards/{boardId}/columns/{columnId}/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping
    public CollectionModel<RepresentationModel<CardResource>> listCards(@PathVariable UUID boardId, @PathVariable UUID columnId) {
        var columns = cardService.listByBoardIdAndColumnId(boardId, columnId)
                .stream()
                .map(card -> new CardModelAssembler().toModel(card))
                .toList();

        return CollectionModel.of(
                columns,
                linkTo(methodOn(BoardController.class).listBoards()).withRel("boards"),
                linkTo(methodOn(BoardController.class).getBoard(boardId)).withRel("board"),
                linkTo(methodOn(ColumnController.class).listColumns(boardId)).withRel("columns"),
                linkTo(methodOn(ColumnController.class).getColumn(boardId, columnId)).withRel("column"),
                linkTo(methodOn(CardController.class).listCards(boardId, columnId)).withSelfRel()
        );
    }

    @PostMapping
    public ResponseEntity<RepresentationModel<CardResource>> createCard(@PathVariable UUID boardId, @PathVariable UUID columnId, @RequestBody @Valid CreateCardRequest card) {
        var createdCard = cardService.createByBoardIdAndColumnId(boardId, columnId, CreateCardDto.fromRequest(card));

        var resource = new CardModelAssembler().toModel(createdCard);
        return ResponseEntity.created(resource.getRequiredLink("self").toUri()).body(resource);
    }

    @GetMapping("/{cardId}")
    public RepresentationModel<CardResource> getCard(@PathVariable UUID boardId, @PathVariable UUID columnId, @PathVariable UUID cardId) {
        var card = cardService.getByBoardIdAndColumnIdAndId(boardId, columnId, cardId);

        return new CardModelAssembler().toModel(card);
    }

    @PutMapping("/{cardId}")
    public RepresentationModel<CardResource> updateCard(@PathVariable UUID boardId, @PathVariable UUID columnId, @PathVariable UUID cardId, @RequestBody @Valid UpdateCardRequest card) {
        var updatedCard = cardService.updateByBoardIdAndColumnIdAndId(boardId, columnId, cardId, UpdateCardDto.fromRequest(card));

        return new CardModelAssembler().toModel(updatedCard);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable UUID boardId, @PathVariable UUID columnId, @PathVariable UUID cardId) {
        cardService.deleteByBoardIdAndColumnIdAndId(boardId, columnId, cardId);

        return ResponseEntity.noContent().build();
    }
}

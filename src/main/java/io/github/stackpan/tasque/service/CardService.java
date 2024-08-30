package io.github.stackpan.tasque.service;

import io.github.stackpan.tasque.data.CreateCardDto;
import io.github.stackpan.tasque.entity.Card;

import java.util.List;
import java.util.UUID;

public interface CardService {

    List<Card> listByBoardIdAndColumnId(UUID boardId, UUID columnId);

    Card createByBoardIdAndColumnId(UUID boardId, UUID columnId, CreateCardDto data);

    Card getByBoardIdAndColumnIdAndId(UUID boardId, UUID columnId, UUID cardId);
}

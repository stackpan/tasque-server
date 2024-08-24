package io.github.stackpan.tasque.service;

import io.github.stackpan.tasque.entity.Card;

import java.util.UUID;

public interface CardService {

    Card getByBoardIdAndColumnIdAndId(UUID boardId, UUID columnId, UUID cardId);

}

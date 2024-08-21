package io.github.stackpan.tasque.service;

import io.github.stackpan.tasque.data.CreateColumnDto;
import io.github.stackpan.tasque.data.UpdateColumnDto;
import io.github.stackpan.tasque.entity.BoardColumn;

import java.util.List;
import java.util.UUID;

public interface ColumnService {

    List<BoardColumn> listByBoardId(UUID boardId);

    BoardColumn getByBoardIdAndId(UUID boardId, UUID columnId);

    BoardColumn createByBoardId(UUID boardId, CreateColumnDto data);

    BoardColumn updateByBoardIdAndId(UUID boardId, UUID columnId, UpdateColumnDto data);

    void deleteByBoardIdAndId(UUID boardId, UUID columnId);
}

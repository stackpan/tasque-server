package io.github.stackpan.tasque.service;

import io.github.stackpan.tasque.entity.BoardColumn;

import java.util.List;
import java.util.UUID;

public interface ColumnService {

    List<BoardColumn> listByBoardId(UUID boardId, UUID userId);

}

package io.github.stackpan.tasque.service;

import io.github.stackpan.tasque.data.CreateBoardDto;
import io.github.stackpan.tasque.data.UpdateBoardDto;
import io.github.stackpan.tasque.entity.Board;

import java.util.List;
import java.util.UUID;

public interface BoardService {

    List<Board> list();

    Board create(CreateBoardDto data);

    Board getById(UUID boardId);

    Board updateById(UUID boardId, UpdateBoardDto data);

    void deleteById(UUID boardId);
}

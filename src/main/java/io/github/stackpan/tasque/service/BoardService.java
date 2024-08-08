package io.github.stackpan.tasque.service;

import io.github.stackpan.tasque.data.CreateBoardDto;
import io.github.stackpan.tasque.data.UpdateBoardDto;
import io.github.stackpan.tasque.entity.Board;

import java.util.List;
import java.util.UUID;

public interface BoardService {

    List<Board> listAsUser(UUID userId);

    Board createAsUser(CreateBoardDto data, UUID userId);

    Board getAsUser(UUID boardId, UUID userId);

    Board updateByIdAsUser(UUID boardId, UpdateBoardDto data, UUID userId);

    void deleteByIdAsUser(UUID boardId, UUID userId);
}

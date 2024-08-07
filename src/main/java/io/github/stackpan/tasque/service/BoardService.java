package io.github.stackpan.tasque.service;

import io.github.stackpan.tasque.entity.Board;

import java.util.List;
import java.util.UUID;

public interface BoardService {

    List<Board> listByUserId(UUID userId);

    Board getById(UUID boardId);

}

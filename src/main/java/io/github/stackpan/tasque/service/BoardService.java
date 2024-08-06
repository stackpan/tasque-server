package io.github.stackpan.tasque.service;

import io.github.stackpan.tasque.entity.Board;

import java.util.UUID;

public interface BoardService {

    Board getById(UUID boardId);

}

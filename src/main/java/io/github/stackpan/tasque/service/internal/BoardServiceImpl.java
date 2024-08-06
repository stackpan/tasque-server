package io.github.stackpan.tasque.service.internal;

import io.github.stackpan.tasque.entity.Board;
import io.github.stackpan.tasque.repository.BoardRepository;
import io.github.stackpan.tasque.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;

    @Override
    public Board getById(UUID boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}

package io.github.stackpan.tasque.service.internal;

import io.github.stackpan.tasque.entity.BoardColumn;
import io.github.stackpan.tasque.repository.ColumnRepository;
import io.github.stackpan.tasque.service.ColumnService;
import io.github.stackpan.tasque.service.util.BoardServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ColumnServiceImpl implements ColumnService {

    private final ColumnRepository columnRepository;

    private final BoardServiceUtil boardServiceUtil;

    @Override
    public List<BoardColumn> listByBoardId(UUID boardId, UUID userId) {
        var board = boardServiceUtil.findByIdOrThrowsNotFound(boardId);
        boardServiceUtil.authorizeOrThrowsNotFound(board, userId);

        return columnRepository.findAllByBoard(board);
    }

    @Override
    public BoardColumn getByBoardIdAndId(UUID boardId, UUID columnId, UUID userId) {
        var board = boardServiceUtil.findByIdOrThrowsNotFound(boardId);
        boardServiceUtil.authorizeOrThrowsNotFound(board, userId);

        return columnRepository.findByBoardAndId(board, columnId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}

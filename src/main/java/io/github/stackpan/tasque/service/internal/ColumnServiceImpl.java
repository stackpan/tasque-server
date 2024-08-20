package io.github.stackpan.tasque.service.internal;

import io.github.stackpan.tasque.data.CreateColumnDto;
import io.github.stackpan.tasque.data.UpdateColumnDto;
import io.github.stackpan.tasque.entity.Board;
import io.github.stackpan.tasque.entity.BoardColumn;
import io.github.stackpan.tasque.repository.BoardRepository;
import io.github.stackpan.tasque.repository.ColumnRepository;
import io.github.stackpan.tasque.service.ColumnService;
import io.github.stackpan.tasque.service.util.BoardServiceUtil;
import io.github.stackpan.tasque.service.util.ColumnServiceUtil;
import jakarta.transaction.Transactional;
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

    private final ColumnServiceUtil columnServiceUtil;
    private final BoardRepository boardRepository;

    @Override
    public List<BoardColumn> listByBoardId(UUID boardId, UUID userId) {
        var board = boardServiceUtil.findByIdOrThrowsNotFound(boardId);
        boardServiceUtil.authorizeOrThrowsNotFound(board, userId);

        return columnRepository.findAllByBoardOrderByPosition(board);
    }

    @Override
    public BoardColumn getByBoardIdAndId(UUID boardId, UUID columnId, UUID userId) {
        var board = boardServiceUtil.findByIdOrThrowsNotFound(boardId);
        boardServiceUtil.authorizeOrThrowsNotFound(board, userId);

        return columnRepository.findByBoardAndId(board, columnId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public BoardColumn createByBoardId(UUID boardId, CreateColumnDto data, UUID userId) {
        var board = boardServiceUtil.findByIdOrThrowsNotFound(boardId);
        boardServiceUtil.authorizeOrThrowsNotFound(board, userId);

        var newColumn = new BoardColumn();
        newColumn.setBoard(board);
        newColumn.setName(data.name());
        newColumn.setDescription(data.description().orElse(null));
        newColumn.setColorHex(data.colorHex().orElse(null));

        var position = columnRepository.maxPositionByBoard(board)
                .map(highest -> data.position()
                        .map(input -> {
                            if (input > highest) {
                                return highest + 1;
                            }
                            columnRepository.shiftColumnsByBoardFromStartingPosition(board, input);
                            return input;
                        })
                        .orElse(highest + 1)
                )
                .orElse(0L);

        newColumn.setPosition(position);
        return columnRepository.save(newColumn);
    }

    @Override
    @Transactional
    public BoardColumn updateByBoardIdAndId(UUID boardId, UUID columnId, UpdateColumnDto data, UUID userId) {
        var board = boardServiceUtil.findByIdOrThrowsNotFound(boardId);
        boardServiceUtil.authorizeOrThrowsNotFound(board, userId);

        var column = columnServiceUtil.findByIdOrThrowsNotFound(columnId);
        column.setName(data.name());
        column.setDescription(data.description());
        column.setColorHex(data.colorHex());

        this.swapColumnsByBoard(board, column.getPosition(), data.position());
        column.setPosition(data.position());

        return columnRepository.save(column);
    }

    private void swapColumnsByBoard(Board board, long originPosition, long destinationPosition) {
        var deltaPosition = destinationPosition - originPosition;

        if (deltaPosition < 0) {
            columnRepository.shiftColumnsByBoardAndRangedPosition(board, destinationPosition, destinationPosition - deltaPosition);
        } else if (deltaPosition > 0) {
            columnRepository.unshiftColumnsByBoardAndRangedPosition(board, destinationPosition + 1, destinationPosition + deltaPosition);
        }
    }
}

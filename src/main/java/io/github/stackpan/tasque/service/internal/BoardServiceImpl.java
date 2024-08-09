package io.github.stackpan.tasque.service.internal;

import io.github.stackpan.tasque.data.CreateBoardDto;
import io.github.stackpan.tasque.data.UpdateBoardDto;
import io.github.stackpan.tasque.entity.Board;
import io.github.stackpan.tasque.entity.User;
import io.github.stackpan.tasque.repository.BoardRepository;
import io.github.stackpan.tasque.service.BoardService;
import io.github.stackpan.tasque.service.util.BoardServiceUtil;
import io.github.stackpan.tasque.service.util.UserServiceUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;

    private final BoardServiceUtil boardServiceUtil;

    private final UserServiceUtil userServiceUtil;

    @Override
    public List<Board> listAsUser(UUID userId) {
        var user = new User();
        user.setId(userId);

        return boardRepository.findAllByOwner(user);
    }

    @Override
    @Transactional
    public Board createAsUser(CreateBoardDto data, UUID userId) {
        var newBoard = new Board();
        newBoard.setName(data.name());
        newBoard.setDescription(data.description().orElse(null));
        newBoard.setColorHex(data.colorHex().orElse(null));

        var user = userServiceUtil.findByIdOrThrowsUnauthorized(userId);
        newBoard.setOwner(user);

        return boardRepository.save(newBoard);
    }

    @Override
    public Board getAsUser(UUID boardId, UUID userId) {
        var board = boardServiceUtil.findByIdOrThrowsNotFound(boardId);
        boardServiceUtil.authorizeOrThrowsNotFound(board, userId);

        return board;
    }

    @Override
    @Transactional
    public Board updateByIdAsUser(UUID boardId, UpdateBoardDto data, UUID userId) {
        var board = boardServiceUtil.findByIdOrThrowsNotFound(boardId);
        boardServiceUtil.authorizeOrThrowsNotFound(board, userId);

        board.setName(data.name());
        board.setDescription(data.description().orElse(null));
        board.setColorHex(data.colorHex().orElse(null));

        return boardRepository.save(board);
    }

    @Override
    public void deleteByIdAsUser(UUID boardId, UUID userId) {
        var board = boardServiceUtil.findByIdOrThrowsNotFound(boardId);
        boardServiceUtil.authorizeOrThrowsNotFound(board, userId);

        boardRepository.delete(board);
    }
}

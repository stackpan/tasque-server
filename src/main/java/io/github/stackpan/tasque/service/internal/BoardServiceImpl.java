package io.github.stackpan.tasque.service.internal;

import io.github.stackpan.tasque.data.CreateBoardDto;
import io.github.stackpan.tasque.data.MoveCardDto;
import io.github.stackpan.tasque.data.UpdateBoardDto;
import io.github.stackpan.tasque.entity.Board;
import io.github.stackpan.tasque.entity.BoardColumn;
import io.github.stackpan.tasque.entity.User;
import io.github.stackpan.tasque.exception.DtoFieldException;
import io.github.stackpan.tasque.repository.BoardRepository;
import io.github.stackpan.tasque.repository.CardRepository;
import io.github.stackpan.tasque.repository.ColumnRepository;
import io.github.stackpan.tasque.security.AuthToken;
import io.github.stackpan.tasque.service.BoardService;
import io.github.stackpan.tasque.service.util.BoardServiceUtil;
import io.github.stackpan.tasque.service.util.UserServiceUtil;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final CardRepository cardRepository;
    private final ColumnRepository columnRepository;

    private final BoardServiceUtil boardServiceUtil;
    private final UserServiceUtil userServiceUtil;

    private final AuthToken authToken;


    @Override
    public List<Board> list() {
        var user = new User();
        user.setId(authToken.getCurrentSubject());

        return boardRepository.findAllByOwner(user);
    }

    @Override
    @Transactional
    public Board create(CreateBoardDto data) {
        var newBoard = new Board();
        newBoard.setName(data.name());
        newBoard.setDescription(data.description().orElse(null));
        newBoard.setColorHex(data.colorHex().orElse(null));

        var user = userServiceUtil.findByIdOrThrowsUnauthorized(authToken.getCurrentSubject());
        newBoard.setOwner(user);

        return boardRepository.save(newBoard);
    }

    @Override
    public Board getById(UUID boardId) {
        return boardServiceUtil.authorizedFindById(boardId);
    }

    @Override
    @Transactional
    public Board updateById(UUID boardId, UpdateBoardDto data) {
        var board = boardServiceUtil.authorizedFindById(boardId);

        board.setName(data.name());
        board.setDescription(data.description().orElse(null));
        board.setColorHex(data.colorHex().orElse(null));

        return boardRepository.save(board);
    }

    @Override
    @Transactional
    public void deleteById(UUID boardId) {
        var board = boardServiceUtil.authorizedFindById(boardId);

        boardRepository.delete(board);
    }

    @Override
    @Transactional
    public List<BoardColumn> moveCard(UUID boardId, MoveCardDto data) {
        var board = boardServiceUtil.authorizedFindById(boardId);

        var cardOptional = cardRepository.findById(data.targetCardId());
        if (cardOptional.isEmpty() || cardOptional.get().getColumn().getBoard().getId() != board.getId()) {
            throw DtoFieldException.withInitial("targetCardId", "The selected Card does not exist.");
        }

        var card = cardOptional.get();
        if (card.getColumn().getId().equals(data.destinationColumnId())) {
            var dtoFieldException = new DtoFieldException();
            dtoFieldException.addError("targetCardId", "The selected Card is a member of the selected Column.");
            dtoFieldException.addError("destinationColumnId", "The selected Column has the selected Card.");
            throw dtoFieldException;
        }

        var columnOptional = columnRepository.findByBoardAndId(board, data.destinationColumnId());
        if (columnOptional.isEmpty()) {
            throw DtoFieldException.withInitial("destinationColumnId", "The selected Column does not exists.");
        }

        var column = columnOptional.get();

        card.setColumn(column);
        cardRepository.save(card);

        return columnRepository.findAllByBoardOrderByPosition(board);
    }
}

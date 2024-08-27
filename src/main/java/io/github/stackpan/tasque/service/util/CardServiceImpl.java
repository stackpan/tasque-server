package io.github.stackpan.tasque.service.util;

import io.github.stackpan.tasque.entity.BoardColumn;
import io.github.stackpan.tasque.entity.Card;
import io.github.stackpan.tasque.repository.CardRepository;
import io.github.stackpan.tasque.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    private final BoardServiceUtil boardServiceUtil;

    @Override
    public List<Card> listByBoardIdAndColumnId(UUID boardId, UUID columnId) {
        var board = this.boardServiceUtil.authorizedFindById(boardId);

        var column = new BoardColumn();
        column.setId(columnId);

        return cardRepository.findAllByParents(board, column);
    }

    @Override
    public Card getByBoardIdAndColumnIdAndId(UUID boardId, UUID columnId, UUID cardId) {
        var board = this.boardServiceUtil.authorizedFindById(boardId);

        var column = new BoardColumn();
        column.setId(columnId);

        return cardRepository.findByIdentities(board, column, cardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}

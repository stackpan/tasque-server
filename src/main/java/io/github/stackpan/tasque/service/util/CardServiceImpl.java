package io.github.stackpan.tasque.service.util;

import io.github.stackpan.tasque.data.CreateCardDto;
import io.github.stackpan.tasque.data.UpdateCardDto;
import io.github.stackpan.tasque.entity.BoardColumn;
import io.github.stackpan.tasque.entity.Card;
import io.github.stackpan.tasque.repository.CardRepository;
import io.github.stackpan.tasque.repository.ColumnRepository;
import io.github.stackpan.tasque.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final ColumnRepository columnRepository;

    private final BoardServiceUtil boardServiceUtil;

    @Override
    public List<Card> listByBoardIdAndColumnId(UUID boardId, UUID columnId) {
        var board = boardServiceUtil.authorizedFindById(boardId);

        var column = new BoardColumn();
        column.setId(columnId);

        return cardRepository.findAllByParents(board, column);
    }

    @Override
    @Transactional
    public Card createByBoardIdAndColumnId(UUID boardId, UUID columnId, CreateCardDto data) {
        var board = boardServiceUtil.authorizedFindById(boardId);
        var column = columnRepository.findByBoardAndId(board, columnId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var card = new Card();
        card.setColumn(column);
        card.setBody(data.body());
        card.setColorHex(data.colorHex().orElse(null));

        return cardRepository.save(card);
    }

    @Override
    public Card getByBoardIdAndColumnIdAndId(UUID boardId, UUID columnId, UUID cardId) {
        var board = boardServiceUtil.authorizedFindById(boardId);

        var column = new BoardColumn();
        column.setId(columnId);

        return cardRepository.findByIdentities(board, column, cardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public Card updateByBoardIdAndColumnIdAndId(UUID boardId, UUID columnId, UUID cardId, UpdateCardDto data) {
        var board = boardServiceUtil.authorizedFindById(boardId);

        var column = new BoardColumn();
        column.setId(columnId);

        var card = cardRepository.findByIdentities(board, column, cardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        card.setBody(data.body());
        card.setColorHex(data.colorHex().orElse(null));

        return cardRepository.save(card);
    }

    @Override
    @Transactional
    public void deleteByBoardIdAndColumnIdAndId(UUID boardId, UUID columnId, UUID cardId) {
        var board = boardServiceUtil.authorizedFindById(boardId);

        var column = new BoardColumn();
        column.setId(columnId);

        var card = cardRepository.findByIdentities(board, column, cardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        cardRepository.delete(card);
    }
}

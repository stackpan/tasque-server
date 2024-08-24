package io.github.stackpan.tasque.service.util;

import io.github.stackpan.tasque.entity.BoardColumn;
import io.github.stackpan.tasque.entity.Card;
import io.github.stackpan.tasque.repository.CardRepository;
import io.github.stackpan.tasque.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    private final BoardServiceUtil boardServiceUtil;

    @Override
    public Card getByBoardIdAndColumnIdAndId(UUID boardId, UUID columnId, UUID cardId) {
        var board = this.boardServiceUtil.authorizedFindById(boardId);

        var column = new BoardColumn();
        column.setId(columnId);

        return cardRepository.findByIdentities(board, column, cardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}

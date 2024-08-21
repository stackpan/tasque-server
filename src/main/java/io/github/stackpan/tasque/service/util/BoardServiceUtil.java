package io.github.stackpan.tasque.service.util;

import io.github.stackpan.tasque.entity.Board;
import io.github.stackpan.tasque.entity.User;
import io.github.stackpan.tasque.repository.BoardRepository;
import io.github.stackpan.tasque.repository.UserRepository;
import io.github.stackpan.tasque.security.AuthToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BoardServiceUtil {

    private final UserRepository userRepository;

    private final BoardRepository boardRepository;

    private final AuthToken authToken;

    public Board authorizedFindById(UUID boardId) {
        var user = new User();
        user.setId(authToken.getCurrentSubject());

        return boardRepository.findByIdAndOwner(boardId, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Board findByIdOrThrowsNotFound(UUID boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public void authorizeOrThrowsNotFound(Board board, UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (!board.getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

}

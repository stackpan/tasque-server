package io.github.stackpan.tasque.service.internal;

import io.github.stackpan.tasque.data.CreateBoardDto;
import io.github.stackpan.tasque.entity.Board;
import io.github.stackpan.tasque.entity.User;
import io.github.stackpan.tasque.repository.BoardRepository;
import io.github.stackpan.tasque.repository.UserRepository;
import io.github.stackpan.tasque.service.BoardService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final UserRepository userRepository;

    private final BoardRepository boardRepository;

    @Override
    public List<Board> listByUserId(UUID userId) {
        var user = new User();
        user.setId(userId);

        return boardRepository.findAllByOwner(user);
    }

    @Override
    @Transactional
    public Board createByUserId(CreateBoardDto data, UUID userId) {
        var newBoard = new Board();
        newBoard.setName(data.name());
        newBoard.setDescription(data.description());
        newBoard.setColorHex(data.colorHex());

        var user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        newBoard.setOwner(user);

        return boardRepository.save(newBoard);
    }

    @Override
    public Board getById(UUID boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}

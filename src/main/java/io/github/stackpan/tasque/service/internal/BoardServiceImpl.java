package io.github.stackpan.tasque.service.internal;

import io.github.stackpan.tasque.data.CreateBoardDto;
import io.github.stackpan.tasque.data.UpdateBoardDto;
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
        newBoard.setDescription(data.description());
        newBoard.setColorHex(data.colorHex());

        var user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        newBoard.setOwner(user);

        return boardRepository.save(newBoard);
    }

    @Override
    public Board getAsUser(UUID boardId, UUID userId) {
        var board = boardRepository.findById(boardId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        var user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        System.out.println(board.getOwner());
        System.out.println(user);
        if (!board.getOwner().equals(user)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return boardRepository.findById(boardId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public Board updateByIdAsUser(UUID boardId, UpdateBoardDto data, UUID userId) {
        var board = boardRepository.findById(boardId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        var user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (!board.getOwner().equals(user)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        board.setName(data.name());
        board.setDescription(data.description());
        board.setColorHex(data.colorHex());

        return boardRepository.save(board);
    }
}

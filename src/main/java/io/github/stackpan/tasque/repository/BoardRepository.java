package io.github.stackpan.tasque.repository;

import io.github.stackpan.tasque.entity.Board;
import io.github.stackpan.tasque.entity.BoardOwner;
import io.github.stackpan.tasque.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoardRepository extends CrudRepository<Board, UUID> {

    List<Board> findAllByOwnerAndDeletedAtIsNull(User user);

    Optional<Board> findByIdAndOwnerAndDeletedAtIsNull(UUID boardId, User user);
}

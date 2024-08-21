package io.github.stackpan.tasque.repository;

import io.github.stackpan.tasque.entity.Board;
import io.github.stackpan.tasque.entity.BoardOwner;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoardRepository extends CrudRepository<Board, UUID> {

    List<Board> findAllByOwner(BoardOwner boardOwner);

    Optional<Board> findByIdAndOwner(UUID id, BoardOwner boardOwner);

}

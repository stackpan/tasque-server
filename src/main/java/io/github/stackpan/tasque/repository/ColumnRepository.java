package io.github.stackpan.tasque.repository;

import io.github.stackpan.tasque.entity.Board;
import io.github.stackpan.tasque.entity.BoardColumn;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ColumnRepository extends CrudRepository<BoardColumn, UUID> {

    List<BoardColumn> findAllByBoard(Board board);

}

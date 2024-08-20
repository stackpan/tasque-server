package io.github.stackpan.tasque.repository;

import io.github.stackpan.tasque.entity.Board;
import io.github.stackpan.tasque.entity.BoardColumn;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ColumnRepository extends CrudRepository<BoardColumn, UUID> {

    List<BoardColumn> findAllByBoardOrderByPosition(Board board);

    Optional<BoardColumn> findByBoardAndId(Board board, UUID id);

    Long countByBoard(Board board);

    @Query("SELECT MAX(c.position) FROM Column c WHERE c.board = ?1")
    Optional<Long> maxPositionByBoard(Board board);

    @Modifying
    @Query("UPDATE Column c SET c.position = c.position + 1 WHERE c.board = ?1 AND c.position >= ?2")
    void shiftColumnsByBoardFromStartingPosition(Board board, Long position);

}

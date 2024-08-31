package io.github.stackpan.tasque.repository;

import io.github.stackpan.tasque.entity.Board;
import io.github.stackpan.tasque.entity.BoardColumn;
import io.github.stackpan.tasque.entity.Card;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends CrudRepository<Card, UUID> {

    @Query("SELECT c FROM Card c WHERE c.column.board = ?1 AND c.column = ?2")
    List<Card> findAllByParents(Board board, BoardColumn column);

    @Query("SELECT c FROM Card c WHERE c.column.board = ?1 AND c.column = ?2 AND c.id = ?3")
    Optional<Card> findByIdentities(Board board, BoardColumn column, UUID cardId);

}

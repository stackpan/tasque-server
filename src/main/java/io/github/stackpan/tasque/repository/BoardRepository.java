package io.github.stackpan.tasque.repository;

import io.github.stackpan.tasque.entity.Board;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface BoardRepository extends CrudRepository<Board, UUID> {

}

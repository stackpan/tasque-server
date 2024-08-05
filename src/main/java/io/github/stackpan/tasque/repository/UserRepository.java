package io.github.stackpan.tasque.repository;

import io.github.stackpan.tasque.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {

    @Query("select u from User u where u.username = :principal or u.email = :principal")
    Optional<User> findByPrincipal(@Param("principal") String principal);

}

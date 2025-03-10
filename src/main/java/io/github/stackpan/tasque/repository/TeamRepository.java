package io.github.stackpan.tasque.repository;

import io.github.stackpan.tasque.entity.Team;
import io.github.stackpan.tasque.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {

    @Query("select t from Team t join TeamMember tm on tm.team = t where tm.user = ?1 and t.deletedAt is null")
    Collection<Team> getByUser(User user);

    @Query("select t from Team t join TeamMember tm on tm.team = t where t.id = ?1 and tm.user = ?2 and t.deletedAt is null")
    Optional<Team> findByIdAndUser(UUID teamId, User user);

    @Modifying
    @Query("update Team t set t.deletedAt = current_timestamp where t.id = :teamId")
    int softDeleteById(@Param("teamId") UUID teamId);
}

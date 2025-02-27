package io.github.stackpan.tasque.repository;

import io.github.stackpan.tasque.entity.Team;
import io.github.stackpan.tasque.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {

    @Query("select t from Team t join TeamMember tm on tm.team = t where tm.user = ?1")
    Collection<Team> getByUser(User user);

}

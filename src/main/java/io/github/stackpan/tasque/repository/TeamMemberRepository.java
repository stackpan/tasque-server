package io.github.stackpan.tasque.repository;

import io.github.stackpan.tasque.entity.TeamMember;
import io.github.stackpan.tasque.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TeamMemberRepository extends JpaRepository<TeamMember, UUID> {

    List<TeamMember> getByUser(User user);
}

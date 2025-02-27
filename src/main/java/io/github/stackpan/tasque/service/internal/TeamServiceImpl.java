package io.github.stackpan.tasque.service.internal;

import io.github.stackpan.tasque.entity.Team;
import io.github.stackpan.tasque.entity.User;
import io.github.stackpan.tasque.repository.TeamMemberRepository;
import io.github.stackpan.tasque.repository.TeamRepository;
import io.github.stackpan.tasque.security.AuthToken;
import io.github.stackpan.tasque.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;

    private final TeamMemberRepository teamMemberRepository;

    private final AuthToken authToken;

    @Override
    public List<Team> getTeams() {
        var user = new User();
        user.setId(authToken.getCurrentSubject());

        return (List<Team>) teamRepository.getByUser(user);
    }
}

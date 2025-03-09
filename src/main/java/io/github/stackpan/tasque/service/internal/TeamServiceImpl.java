package io.github.stackpan.tasque.service.internal;

import io.github.stackpan.tasque.data.CreateTeamDto;
import io.github.stackpan.tasque.data.UpdateTeamDto;
import io.github.stackpan.tasque.entity.Team;
import io.github.stackpan.tasque.entity.TeamMember;
import io.github.stackpan.tasque.entity.User;
import io.github.stackpan.tasque.repository.TeamMemberRepository;
import io.github.stackpan.tasque.repository.TeamRepository;
import io.github.stackpan.tasque.security.AuthToken;
import io.github.stackpan.tasque.service.TeamService;
import io.github.stackpan.tasque.service.util.TeamServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;

    private final TeamMemberRepository teamMemberRepository;

    private final TeamServiceUtil teamServiceUtil;

    private final AuthToken authToken;

    @Override
    public List<Team> getTeams() {
        var user = new User();
        user.setId(authToken.getCurrentSubject());

        return (List<Team>) teamRepository.getByUser(user);
    }

    @Override
    @Transactional
    public Team createTeam(CreateTeamDto data) {
        var team = new Team();
        team.setName(data.name());
        team.setDescription(data.description().orElse(null));

        var createdTeam = teamRepository.save(team);

        var user = new User();
        user.setId(authToken.getCurrentSubject());

        var teamMember = new TeamMember();
        teamMember.setUser(user);
        teamMember.setTeam(team);
        teamMember.setRole(TeamMember.Role.OWNER);

        teamMemberRepository.save(teamMember);

        return createdTeam;
    }

    @Override
    public Team getById(UUID teamId) {
        return teamServiceUtil.authorizedFindById(teamId);
    }

    @Override
    @Transactional
    public Team updateById(UUID teamId, UpdateTeamDto data) {
        var team = teamServiceUtil.authorizedFindById(teamId);
        team.setName(data.name());
        team.setDescription(data.description().orElse(null));

        return teamRepository.save(team);
    }
}

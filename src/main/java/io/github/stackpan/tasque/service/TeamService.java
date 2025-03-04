package io.github.stackpan.tasque.service;

import io.github.stackpan.tasque.data.CreateTeamDto;
import io.github.stackpan.tasque.entity.Team;

import java.util.List;
import java.util.UUID;

public interface TeamService {

    List<Team> getTeams();

    Team createTeam(CreateTeamDto data);

    Team getById(UUID teamId);

}

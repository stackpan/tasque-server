package io.github.stackpan.tasque.service;

import io.github.stackpan.tasque.entity.Team;

import java.util.List;
import java.util.UUID;

public interface TeamService {

    List<Team> getTeams();

    Team getById(UUID teamId);

}

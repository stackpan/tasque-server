package io.github.stackpan.tasque.http.controller;

import io.github.stackpan.tasque.http.resource.TeamResource;
import io.github.stackpan.tasque.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    public List<TeamResource> getTeams() {
        return teamService.getTeams()
                .stream()
                .map(TeamResource::fromEntity)
                .toList();
    }

}

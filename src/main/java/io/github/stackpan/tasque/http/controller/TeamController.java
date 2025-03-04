package io.github.stackpan.tasque.http.controller;

import io.github.stackpan.tasque.data.CreateTeamDto;
import io.github.stackpan.tasque.http.assembler.TeamModelAssembler;
import io.github.stackpan.tasque.http.request.CreateTeamRequest;
import io.github.stackpan.tasque.http.resource.TeamResource;
import io.github.stackpan.tasque.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    public CollectionModel<RepresentationModel<TeamResource>> getTeams() {
        var teams = teamService.getTeams()
                .stream()
                .map(team -> new TeamModelAssembler().toModel(team))
                .toList();

        return CollectionModel.of(
                teams,
                linkTo(methodOn(TeamController.class).getTeams()).withSelfRel()
        );
    }

    @PostMapping
    public ResponseEntity<RepresentationModel<TeamResource>> createTeam(@RequestBody @Valid CreateTeamRequest request) {
        var createdTeam = teamService.createTeam(CreateTeamDto.fromRequest(request));

        var model = new TeamModelAssembler().toModel(createdTeam);
        return ResponseEntity.created(model.getRequiredLink("self").toUri()).body(model);
    }

    @GetMapping("/{teamId}")
    public RepresentationModel<TeamResource> getTeam(@PathVariable UUID teamId) {
        var team = teamService.getById(teamId);

        return new TeamModelAssembler().toModel(team);
    }
}

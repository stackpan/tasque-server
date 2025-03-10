package io.github.stackpan.tasque.http.assembler;

import io.github.stackpan.tasque.entity.Team;
import io.github.stackpan.tasque.http.controller.TeamController;
import io.github.stackpan.tasque.http.resource.TeamResource;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class TeamModelAssembler implements RepresentationModelAssembler<Team, RepresentationModel<TeamResource>> {
    @Override
    public RepresentationModel<TeamResource> toModel(Team entity) {
        var teamResource = TeamResource.fromEntity(entity);

        return HalModelBuilder.halModelOf(teamResource)
                .link(linkTo(methodOn(TeamController.class).getTeam(entity.getId())).withSelfRel())
                .build();
    }
}

package io.github.stackpan.tasque.http.assembler;

import io.github.stackpan.tasque.entity.Team;
import io.github.stackpan.tasque.http.resource.TeamResource;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

public class TeamModelAssembler implements RepresentationModelAssembler<Team, EntityModel<TeamResource>> {
    @Override
    public EntityModel<TeamResource> toModel(Team entity) {
        return EntityModel.of(TeamResource.fromEntity(entity));
    }
}

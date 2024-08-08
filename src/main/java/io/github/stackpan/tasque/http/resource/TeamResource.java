package io.github.stackpan.tasque.http.resource;

import io.github.stackpan.tasque.entity.Team;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Value
public class TeamResource extends RepresentationModel<TeamResource> {

    UUID id;

    public static TeamResource fromEntity(Team entity) {
        return new TeamResource(entity.getId());
    }
}

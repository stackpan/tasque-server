package io.github.stackpan.tasque.http.assembler;

import io.github.stackpan.tasque.entity.User;
import io.github.stackpan.tasque.http.controller.UserController;
import io.github.stackpan.tasque.http.resource.UserResource;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class UserModelAssembler implements RepresentationModelAssembler<User, RepresentationModel<UserResource>> {
    @Override
    public RepresentationModel<UserResource> toModel(User entity) {
        return HalModelBuilder.halModelOf(UserResource.fromEntity(entity))
                .link(linkTo(methodOn(UserController.class).getUser(entity.getId())).withSelfRel())
                .build();
    }
}

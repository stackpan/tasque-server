package io.github.stackpan.tasque.http.assembler;

import io.github.stackpan.tasque.entity.User;
import io.github.stackpan.tasque.http.controller.UserController;
import io.github.stackpan.tasque.http.resource.UserResource;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class UserModelAssembler implements RepresentationModelAssembler<User, EntityModel<UserResource>> {

    @Override
    public EntityModel<UserResource> toModel(User entity) {
        return EntityModel.of(
                UserResource.fromEntity(entity),
                linkTo(methodOn(UserController.class).getUser(entity.getId().toString())).withSelfRel()
        );
    }
}

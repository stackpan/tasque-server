package io.github.stackpan.tasque.http.assembler;

import io.github.stackpan.tasque.entity.User;
import io.github.stackpan.tasque.http.controller.AuthController;
import io.github.stackpan.tasque.http.controller.UserController;
import io.github.stackpan.tasque.http.resource.UserResource;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class AuthModelAssembler implements RepresentationModelAssembler<User, EntityModel<UserResource>> {

    @Override
    public EntityModel<UserResource> toModel(User entity) {
        return EntityModel.of(
                UserResource.fromEntity(entity),
                linkTo(methodOn(AuthController.class).me(null)).withSelfRel(),
                linkTo(methodOn(AuthController.class).upload()).withRel("upload"),
                linkTo(methodOn(AuthController.class).changePassword()).withRel("changePassword"),
                linkTo(methodOn(UserController.class).getUser(entity.getId())).withRel("user")
        );
    }
}

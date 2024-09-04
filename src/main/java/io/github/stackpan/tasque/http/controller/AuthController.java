package io.github.stackpan.tasque.http.controller;

import io.github.stackpan.tasque.data.AuthLoginDto;
import io.github.stackpan.tasque.http.assembler.AuthModelAssembler;
import io.github.stackpan.tasque.http.request.LoginRequest;
import io.github.stackpan.tasque.http.resource.AuthResource;
import io.github.stackpan.tasque.http.resource.UserResource;
import io.github.stackpan.tasque.service.AuthService;
import io.github.stackpan.tasque.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final UserService userService;

    @PostMapping("/login")
    public EntityModel<AuthResource> login(@RequestBody @Valid LoginRequest request) {
        var jwt = authService.login(AuthLoginDto.fromRequest(request));
        var resource = AuthResource.fromToken(jwt);

        return EntityModel.of(resource, linkTo(methodOn(AuthController.class).me()).withRel("me"));
    }

    @GetMapping("/me")
    public EntityModel<UserResource> me() {
        var user = userService.getMe();

        return new AuthModelAssembler().toModel(user);
    }

    @PatchMapping("/me/upload")
    public Object upload() {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/me/change-password")
    public Object changePassword() {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}

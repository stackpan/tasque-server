package io.github.stackpan.tasque.http.controller;

import io.github.stackpan.tasque.data.AuthLoginDto;
import io.github.stackpan.tasque.http.request.LoginRequest;
import io.github.stackpan.tasque.http.resource.AuthResource;
import io.github.stackpan.tasque.http.resource.UserResource;
import io.github.stackpan.tasque.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public EntityModel<AuthResource> login(@RequestBody @Valid LoginRequest request) {
        var jwt = authService.login(AuthLoginDto.fromRequest(request));
        var resource = AuthResource.fromJwt(jwt);

        return EntityModel.of(resource, linkTo(methodOn(AuthController.class).me(null)).withRel("me"));
    }

    @GetMapping("/me")
    public EntityModel<UserResource> me(JwtAuthenticationToken token) {
        return null;
    }
}

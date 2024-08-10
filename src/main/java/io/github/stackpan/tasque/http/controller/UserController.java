package io.github.stackpan.tasque.http.controller;

import io.github.stackpan.tasque.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/{userId}")
    public User getUser(@PathVariable String userId) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

}

package io.github.stackpan.tasque.service.util;

import io.github.stackpan.tasque.entity.User;
import io.github.stackpan.tasque.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserServiceUtil {

    private final UserRepository userRepository;

    public User findByIdOrThrowsUnauthorized(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

}

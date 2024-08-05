package io.github.stackpan.tasque.service.internal;

import io.github.stackpan.tasque.entity.User;
import io.github.stackpan.tasque.repository.UserRepository;
import io.github.stackpan.tasque.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public UserDetails loadUserByUsername(String principal) throws UsernameNotFoundException {
        return userRepository.findByPrincipal(principal)
                .orElseThrow(() -> new UsernameNotFoundException("Could not find user with principal: %s".formatted(principal)));
    }
}

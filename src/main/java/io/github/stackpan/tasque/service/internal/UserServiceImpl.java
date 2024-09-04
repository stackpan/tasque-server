package io.github.stackpan.tasque.service.internal;

import io.github.stackpan.tasque.entity.User;
import io.github.stackpan.tasque.repository.UserRepository;
import io.github.stackpan.tasque.security.AuthToken;
import io.github.stackpan.tasque.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    private final AuthToken authToken;

    @Override
    public UserDetails loadUserByUsername(String principal) throws UsernameNotFoundException {
        return userRepository.findByPrincipal(principal)
                .orElseThrow(() -> new UsernameNotFoundException("Could not find user with principal: %s".formatted(principal)));
    }

    @Override
    public User getMe() {
        return userRepository.findById(authToken.getCurrentSubject())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
    }
}

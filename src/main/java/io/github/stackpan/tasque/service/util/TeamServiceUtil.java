package io.github.stackpan.tasque.service.util;

import io.github.stackpan.tasque.entity.Team;
import io.github.stackpan.tasque.entity.User;
import io.github.stackpan.tasque.repository.TeamRepository;
import io.github.stackpan.tasque.security.AuthToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TeamServiceUtil {

    private final TeamRepository teamRepository;

    private final AuthToken authToken;

    public Team authorizedFindById(UUID teamId) {
        var user = new User();
        user.setId(authToken.getCurrentSubject());

        return teamRepository.findByIdAndUser(teamId, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}

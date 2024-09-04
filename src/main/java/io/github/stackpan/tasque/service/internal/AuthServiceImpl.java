package io.github.stackpan.tasque.service.internal;

import io.github.stackpan.tasque.data.AuthLoginDto;
import io.github.stackpan.tasque.security.Tokenizer;
import io.github.stackpan.tasque.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    private final Tokenizer tokenizer;

    @Override
    public String login(AuthLoginDto data) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(data.principal(), data.credential());
        var authenticated = authenticationManager.authenticate(authenticationToken);

        return tokenizer.generate(authenticated);
    }
}

package io.github.stackpan.tasque.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

public interface Tokenizer {

    Jwt generate(Authentication authenticated);

}

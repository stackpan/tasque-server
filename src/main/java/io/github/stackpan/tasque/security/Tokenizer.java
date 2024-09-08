package io.github.stackpan.tasque.security;

import org.springframework.security.core.Authentication;

public interface Tokenizer {

    String generate(Authentication authenticated);

}

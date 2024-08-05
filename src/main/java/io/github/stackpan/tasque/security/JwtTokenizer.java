package io.github.stackpan.tasque.security;

import io.github.stackpan.tasque.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenizer implements Tokenizer {

    private final JwtEncoder jwtEncoder;

    @Value("${jwt.issuer:self}")
    private String jwtClaimsIssuer;

    @Value("${jwt.expiration:3600}")
    private Long jwtClaimsExpiration;

    @Override
    public Jwt generate(Authentication authenticated) {
        var now = Instant.now();
        var scope = authenticated.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer(jwtClaimsIssuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(jwtClaimsExpiration))
                .subject(((User) authenticated.getPrincipal()).getId().toString())
                .claim("scope", scope)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims));
    }
}

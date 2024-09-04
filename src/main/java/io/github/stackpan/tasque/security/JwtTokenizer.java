package io.github.stackpan.tasque.security;

import io.github.stackpan.tasque.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenizer implements Tokenizer {

    private final JwtEncoder jwtEncoder;

    @Value("${jwt.issuer:self}")
    private String jwtClaimsIssuer;

    @Value("${jwt.audience:self}")
    private String jwtClaimsAudience;

    @Value("${jwt.expiration:3600}")
    private Long jwtClaimsExpiration;

    @Override
    public Jwt generate(Authentication authenticated) {
        var now = Instant.now();
        var scope = authenticated.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        var header = JwsHeader.with(MacAlgorithm.HS512)
                .type("JWT")
                .build();

        var claims = JwtClaimsSet.builder()
                .id(UUID.randomUUID().toString())
                .issuer(jwtClaimsIssuer)
                .audience(List.of(jwtClaimsAudience))
                .issuedAt(now)
                .notBefore(now)
                .expiresAt(now.plusSeconds(jwtClaimsExpiration))
                .subject(((User) authenticated.getPrincipal()).getId().toString())
                .claim("scope", scope)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims));
    }
}

package io.github.stackpan.tasque.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.AESEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import io.github.stackpan.tasque.entity.User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JweTokenizer implements Tokenizer {

    private final SecretKey secretKey;

    @Value("${jwt.issuer:self}")
    private String jwtClaimsIssuer;

    @Value("${jwt.audience:self}")
    private String jwtClaimsAudience;

    @Value("${jwt.expiration:3600}")
    private Long jwtClaimsExpiration;

    private JWEEncrypter encrypter;

    @PostConstruct
    public void init() throws KeyLengthException {
        this.encrypter = new AESEncrypter(secretKey);
    }

    @Override
    public String generate(Authentication authenticated) {
        var now = Instant.now();
        var scope = authenticated.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        var header = new JWEHeader(JWEAlgorithm.A256GCMKW, EncryptionMethod.A256GCM);
        var claims = new JWTClaimsSet.Builder()
                .subject(((User) authenticated.getPrincipal()).getId().toString())
                .jwtID(UUID.randomUUID().toString())
                .issuer(jwtClaimsIssuer)
                .audience(jwtClaimsAudience)
                .issueTime(Date.from(now))
                .notBeforeTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(jwtClaimsExpiration)))
                .claim("scope", scope)
                .build();

        var jwe = new EncryptedJWT(header, claims);
        try {
            jwe.encrypt(encrypter);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

        return jwe.serialize();
    }
}

package io.github.stackpan.tasque.config;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.JWEDecryptionKeySelector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

@Configuration
public class TokenizationConfig {

    @Bean
    public SecretKey secretKey() throws NoSuchAlgorithmException {
        var generator = KeyGenerator.getInstance("AES");
        generator.init(EncryptionMethod.A256GCM.cekBitLength());
        return generator.generateKey();
    }

    @Bean
    public JwtDecoder jwtDecoder() throws NoSuchAlgorithmException {
        var jwkSource = new ImmutableJWKSet<>(
                new JWKSet(new OctetSequenceKey.Builder(secretKey()).build())
        );

        return NimbusJwtDecoder.withSecretKey(secretKey())
                .jwtProcessorCustomizer(processor -> processor
                        .setJWEKeySelector(
                                new JWEDecryptionKeySelector<>(JWEAlgorithm.A256GCMKW, EncryptionMethod.A256GCM, jwkSource)
                        )
                )
                .build();
    }

}

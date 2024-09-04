package io.github.stackpan.tasque.config.properties;

import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "rsa")
public record RsaConfigProperties(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
    public RSAKey toJoseRSAKey() {
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .build();
    }
}

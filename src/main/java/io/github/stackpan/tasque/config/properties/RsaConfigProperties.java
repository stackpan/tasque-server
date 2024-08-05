package io.github.stackpan.tasque.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "rsa")
public record RsaConfigProperties(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
}

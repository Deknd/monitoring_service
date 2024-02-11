package com.denknd.security.utils;

import com.denknd.entity.Roles;
import com.denknd.security.entity.Token;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DefaultSerializerTokenTest {

  private JWEEncrypter jweEncrypter;
  private JWEAlgorithm jweAlgorithm;
  private EncryptionMethod encryptionMethod;
  private DefaultSerializerToken serializerToken;
  private String secretCode = "{\"kty\":\"oct\",\"use\":\"enc\",\"k\":\"iGJzlxHw5WqHUVW2LDnsxLkWuUnxclpnhWtaDgvbmt4\"}";

  @BeforeEach
  void setUp() throws ParseException, KeyLengthException {
    jweAlgorithm = JWEAlgorithm.DIR;
    encryptionMethod = EncryptionMethod.A128CBC_HS256;
    jweEncrypter = new DirectEncrypter(OctetSequenceKey.parse(secretCode));
    serializerToken = new DefaultSerializerToken(jweEncrypter, jweAlgorithm, encryptionMethod);
  }

  @Test
  @DisplayName("Проверяет, что токен шифруется и преобразуется в строку")
  void apply() {

    var now = Instant.now();
    var token = Token.builder()
            .id(UUID.randomUUID())
            .userId(5L)
            .firstName("Den")
            .role(Roles.USER.name())
            .createdAt(now)
            .expiresAt(now.plus(Duration.ofHours(3)))
            .build();

    var tokenString = serializerToken.apply(token);

    assertThat(tokenString).isNotNull();


  }
}
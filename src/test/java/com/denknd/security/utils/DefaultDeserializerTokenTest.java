package com.denknd.security.utils;

import com.denknd.entity.Roles;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DefaultDeserializerTokenTest {

  private DefaultDeserializerToken deserializerToken;
  private JWEDecrypter jweDecrypter;

  private final String rawToken = "eyJraWQiOiI4OWQ5MzJhNS1iZDkzLTQ1OWItYTFmOC1lN2RhYWM0MzIwYTUiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..VRq6FYkgeGl8t-Ji2wCtag.EQcXdCjFgnNZW8-yF_3K-8oXRKnK1dFwK5LmcqhkLG3rYluROrlmiO9p_7Mz7k1P8pv_paSF2nGiuKBQUUxRrinXld-QRU4BXSBInbxWzeGZkPhg7lYYEvS8eK1nfMyk5sKUXoHoO-DpxLPTj80BKtA339k9_b1RhXONhBOJJL9JXwVcMLC6Kmin7QUiGIwQ.Ffr4eWb2JngxHSaSulY9AA";

  private final String secretCode = "{\"kty\":\"oct\",\"use\":\"enc\",\"k\":\"iGJzlxHw5WqHUVW2LDnsxLkWuUnxclpnhWtaDgvbmt4\"}";

  @BeforeEach
  void setUp() throws ParseException, KeyLengthException {
    jweDecrypter = new DirectDecrypter(OctetSequenceKey.parse(secretCode));
    deserializerToken = new DefaultDeserializerToken(jweDecrypter);
  }

  @Test
  @DisplayName("Проверяется, что токен дешефруется и десереализуется в объект токен")
  void apply() {
    var createTime = Instant.parse("2024-02-10T02:15:39.370943468Z");


    var token = deserializerToken.apply(rawToken);

    assertThat(token).isNotNull();
    assertThat(token.id()).isNotNull();
    assertThat(token.userId()).isNotNull();
    assertThat(token.role()).isEqualTo(Roles.USER.name());
    assertThat(token.createdAt()).isBefore(createTime);
    assertThat(token.expiresAt()).isAfter(createTime);
  }
}
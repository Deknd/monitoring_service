package com.denknd.security.utils;

import com.denknd.security.entity.Token;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

/**
 * Класс для сериализации токена в строку.
 * Преобразует объект токена в строковое представление, зашифрованное с помощью JWE (JSON Web Encryption).
 */
@RequiredArgsConstructor
@Log4j2
@Component
public class DefaultSerializerToken implements Function<Token, String> {
  private final JWEEncrypter jweEncrypter;

  /**
   * Сераилизует токен в строку.
   *
   * @param token токен для сериализации
   * @return сериализованный и зашифрованный токен
   */
  @Override
  public String apply(Token token) {
    var jwsHeader = new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A256GCM)
            .keyID(token.id().toString())
            .build();
    var claimsSet = new JWTClaimsSet.Builder()
            .jwtID(token.id().toString())
            .subject(token.userId().toString())
            .issueTime(Date.from(token.createdAt()))
            .expirationTime(Date.from(token.expiresAt()))
            .claim("firstName", token.firstName())
            .claim("authorities", token.role())
            .build();
    var encryptedJWT = new EncryptedJWT(jwsHeader, claimsSet);
    try {
      encryptedJWT.encrypt(this.jweEncrypter);

      return encryptedJWT.serialize();
    } catch (JOSEException exception) {
      exception.printStackTrace();
      log.error(exception.getMessage(), exception);
    }

    return null;
  }
}

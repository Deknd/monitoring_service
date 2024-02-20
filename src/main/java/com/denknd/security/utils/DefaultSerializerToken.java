package com.denknd.security.utils;

import com.denknd.security.entity.Token;
import com.nimbusds.jose.*;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.function.Function;

/**
 * Класс для сериализации токена в строку.
 * Преобразует объект токена в строковое представление, зашифрованное с помощью JWE (JSON Web Encryption).
 */
@RequiredArgsConstructor
@Slf4j
public class DefaultSerializerToken implements Function<Token, String> {
    /**
     * Экземпляр JWEEncrypter для шифрования токена.
     */
    private final JWEEncrypter jweEncrypter;
    /**
     * Алгоритм JWE для шифрования.
     */
    private final JWEAlgorithm jweAlgorithm;
    /**
     * Метод шифрования для JWE.
     */
    private final EncryptionMethod encryptionMethod;

    /**
     * Сераилизует токен в строку.
     * @param token токен для сериализации
     * @return сериализованный и зашифрованный токен
     */
    @Override
    public String apply(Token token) {
        var jwsHeader = new JWEHeader.Builder(this.jweAlgorithm, this.encryptionMethod)
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

package com.denknd.security.utils;

import com.denknd.security.entity.Token;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jwt.EncryptedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.text.ParseException;
import java.util.UUID;
import java.util.function.Function;

/**
 * Класс для десериализации из строки в токен
 */
@RequiredArgsConstructor
@Log4j2
public class DefaultDeserializerToken implements Function<String, Token> {
    /**
     * Декриптор для расшифровки токена
     */
    private final  JWEDecrypter jweDecrypter;

    /**
     * Десереализует и расшифровывает токен
     * @param rawToken сырой зашифрованный токен
     * @return объект отвечающий за токен
     */
    @Override
    public Token apply(String rawToken) {
        try {
            var encryptedJWT = EncryptedJWT.parse(rawToken);
            encryptedJWT.decrypt(this.jweDecrypter);
            var claimsSet = encryptedJWT.getJWTClaimsSet();
            Long userId;
            try {
                userId = Long.parseLong(claimsSet.getSubject());
            } catch (NumberFormatException e) {
                log.error(e.getMessage());
                return null;
            }
            return new Token(UUID.fromString(claimsSet.getJWTID()), userId,
                    claimsSet.getStringClaim("firstName"),
                    claimsSet.getStringClaim("authorities"),
                    claimsSet.getIssueTime().toInstant(),
                    claimsSet.getExpirationTime().toInstant());
        } catch (ParseException | JOSEException exception) {
            log.error(exception.getMessage(), exception);
        }
        return null;
    }
}

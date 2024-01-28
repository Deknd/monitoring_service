package com.denknd.adapter.encoder;

import com.denknd.port.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Класс для хеширования и сравнение пароля
 */
public class Sha256Encode implements PasswordEncoder {
    /**
     * Хеширует пароль
     * @param rawPassword сырой пароль @NotNull
     * @return хеш пароля
     */
    @Override
    public String encode(CharSequence rawPassword) {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            var hashedBytes = md.digest(rawPassword.toString().getBytes(StandardCharsets.UTF_8));

            var stringBuilder = new StringBuilder();
            for (byte hashedByte : hashedBytes) {
                stringBuilder.append(Integer.toString((hashedByte & 0xff) + 0x100, 16).substring(1));
            }

            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Сравнивает сырой пароль с хешем
     * @param rawPassword не хешированый пароль @NotNull
     * @param encodedPassword хешированый пароль
     * @return
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        var hashedRawPassword = encode(rawPassword);
        return hashedRawPassword.equals(encodedPassword);
    }
}

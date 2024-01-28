package com.denknd.adapter.encoder;

import com.denknd.port.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256Encode implements PasswordEncoder {
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

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        var hashedRawPassword = encode(rawPassword);
        return hashedRawPassword.equals(encodedPassword);
    }
}

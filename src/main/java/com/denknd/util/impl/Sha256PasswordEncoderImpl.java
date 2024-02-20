package com.denknd.util.impl;

import com.denknd.util.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Реализация интерфейса для хеширования и сравнения паролей с использованием алгоритма SHA-256.
 */
@Component
public class Sha256PasswordEncoderImpl implements PasswordEncoder {
  private static final int BYTE_MASK = 0xff;
  private static final int HEX_PREFIX = 0x100;

  /**
   * Хеширует переданный пароль.
   *
   * @param rawPassword сырой пароль, который необходимо захешировать @NotNull
   * @return хеш пароля
   * @throws NoSuchAlgorithmException если алгоритм хеширования не поддерживается
   */
  @Override
  public String encode(CharSequence rawPassword) throws NoSuchAlgorithmException {
    try {
      var md = MessageDigest.getInstance("SHA-256");
      var hashedBytes = md.digest(rawPassword.toString().getBytes(StandardCharsets.UTF_8));

      var stringBuilder = new StringBuilder();
      for (byte hashedByte : hashedBytes) {
        stringBuilder.append(Integer.toString((hashedByte & BYTE_MASK) + HEX_PREFIX, 16).substring(1));
      }

      return stringBuilder.toString();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * Сравнивает переданный сырой пароль с хешированным паролем.
   *
   * @param rawPassword    сырой пароль, который необходимо сравнить с хешем @NotNull
   * @param hashedPassword хешированный пароль, с которым необходимо сравнить сырой пароль
   * @return true, если пароли совпадают
   */
  @Override
  public boolean matches(CharSequence rawPassword, String hashedPassword) {
    try {
      String hashedRawPassword = encode(rawPassword);
      return hashedRawPassword.equals(hashedPassword);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return false;
    }
  }
}

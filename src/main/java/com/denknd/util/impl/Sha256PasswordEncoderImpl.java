package com.denknd.util.impl;

import com.denknd.util.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Реализация интерфейса для хеширования и сравнения паролей с использованием SHA-256.
 */
public class Sha256PasswordEncoderImpl implements PasswordEncoder {
  private static final int BYTE_MASK = 0xff;
  private static final int HEX_PREFIX = 0x100;

  /**
   * Хеширует пароль.
   *
   * @param rawPassword сырой пароль @NotNull
   * @return хеш пароля
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
   * Сравнивает сырой пароль с хешем.
   *
   * @param rawPassword    не хешированный пароль @NotNull
   * @param hashedPassword хешированный пароль
   * @return true, если пароли равны
   */
  @Override
  public boolean matches(CharSequence rawPassword, String hashedPassword) {
    String hashedRawPassword = null;
    try {
      hashedRawPassword = encode(rawPassword);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return false;
    }
    return hashedRawPassword.equals(hashedPassword);
  }
}

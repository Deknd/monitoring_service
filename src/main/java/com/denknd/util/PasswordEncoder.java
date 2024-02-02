package com.denknd.util;

import java.security.NoSuchAlgorithmException;

/**
 * Интерфейс для хеширования и сравнения паролей.
 */
public interface PasswordEncoder {
  /**
   * Хеширует пароль.
   *
   * @param rawPassword нехешированный пароль
   * @return хеш пароля
   */
  String encode(CharSequence rawPassword) throws NoSuchAlgorithmException;

  /**
   * Сравнивает хеш пароля с нехешированным паролем.
   *
   * @param rawPassword    нехешированный пароль
   * @param hashedPassword хеш пароля
   * @return true, если они равны
   */
  boolean matches(CharSequence rawPassword, String hashedPassword);
}

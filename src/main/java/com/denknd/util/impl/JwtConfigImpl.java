package com.denknd.util.impl;

import com.denknd.util.JwtConfig;
import lombok.Setter;

/**
 * Объект для получения конфигураций для Jwt из application.yml
 */
@Setter
public class JwtConfigImpl implements JwtConfig {
  /**
   * Секретный ключ, для шифрования токена
   */
  private String cookie_token_key;
  /**
   * Время жизни токена в часах
   */
  private Long expiration;

  /**
   * Возвращает секретный ключ.
   *
   * @return секретный ключ
   */
  @Override
  public String secretKey() {
    return this.cookie_token_key;
  }

  /**
   * Время жизни токена в часах
   *
   * @return Число отвечающая за время жизни токена
   */
  @Override
  public Long expiration() {
    return this.expiration;
  }
}

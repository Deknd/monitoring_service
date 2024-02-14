package com.denknd.util;

/**
 *
 */
public interface JwtConfig {
  String secretKey();

  Long expiration();
}

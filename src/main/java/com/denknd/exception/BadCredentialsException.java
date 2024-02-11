package com.denknd.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Выкидывается, когда не верные данные пользователя
 */
@RequiredArgsConstructor
@Getter
public class BadCredentialsException  extends Exception{
  /**
   * Сообщение об ошибке.
   */
  private final String message;
}

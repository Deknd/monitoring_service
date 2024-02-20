package com.denknd.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Выкидывается, когда объект не проходит валидацию
 */
@RequiredArgsConstructor
@Getter
public class ConstraintViolationException extends Exception {
  /**
   * Сообщение об ошибке.
   */
  private final String message;
}

package com.denknd.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Ошибка возникает при сохранении не корректного типа показаний
 */
@RequiredArgsConstructor
@Getter
public class TypeMeterAdditionException extends Exception {
  /**
   * Сообщение об ошибке.
   */
  private final String message;
}

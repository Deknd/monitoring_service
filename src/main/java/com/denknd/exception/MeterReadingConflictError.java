package com.denknd.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Выбрасывается, когда происходит ошибка подачей показаний.
 */
@RequiredArgsConstructor
@Getter
public class MeterReadingConflictError extends Exception {
  /**
   * Сообщение об ошибке.
   */
  private final String message;
}

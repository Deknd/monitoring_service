package com.denknd.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Выбрасывается при не соблюдений ограничений БД при сохранении пользователя
 */
@RequiredArgsConstructor
@Getter
public class InvalidUserDataException extends Exception {
  /**
   * Сообщение об ошибке.
   */
  private final String message;
}

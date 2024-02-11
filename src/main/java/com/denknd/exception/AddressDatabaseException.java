package com.denknd.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Выбрасывается при не соблюдений ограничений БД при сохранении адреса
 */
@RequiredArgsConstructor
@Getter
public class AddressDatabaseException extends Exception{
  /**
   * Сообщение об ошибке.
   */
  private final String message;
}

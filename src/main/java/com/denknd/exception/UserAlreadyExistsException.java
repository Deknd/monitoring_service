package com.denknd.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Эксепшен выбрасывается, при попытке зарегистрировать пользователя с емайлом, который существует.
 */
@RequiredArgsConstructor
@Getter
public class UserAlreadyExistsException extends Exception {
  /**
   * Сообщение об ошибке.
   */
  private final String message;
}

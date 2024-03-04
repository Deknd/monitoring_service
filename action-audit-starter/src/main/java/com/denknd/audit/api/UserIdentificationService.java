package com.denknd.audit.api;

/**
 * Сервис для получения информации о пользователе
 */
public interface UserIdentificationService {
  /**
   * Идентификатор пользователя, который выполняет действие
   *
   * @return идентификатор пользователя
   */
  Long getUserId();
}

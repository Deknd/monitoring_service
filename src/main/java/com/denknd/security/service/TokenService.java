package com.denknd.security.service;

import com.denknd.security.entity.Token;
import com.denknd.security.entity.TokenBlock;
/**
 * Интерфейс сервиса для управления токенами доступа.
 */
public interface TokenService {
  /**
   * Проверяет, заблокирован ли токен с данным айди
   * @param id идентификатор заблокированного токена
   * @return возвращает true, если токен заблокирован
   */
  boolean existsByTokenId(String id);

  /**
   * Блокирует токен
   * @param token токен, который нужно заблокировать
   * @return заблокированный токен
   */
  TokenBlock lockToken(Token token);
}

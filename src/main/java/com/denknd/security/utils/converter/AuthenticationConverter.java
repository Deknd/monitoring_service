package com.denknd.security.utils.converter;

import com.denknd.exception.BadCredentialsException;
import com.denknd.security.entity.PreAuthenticatedAuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Интерфейс для конвертации HTTP-запроса в объект аутентификации.
 */
public interface AuthenticationConverter {
  /**
   * Конвертирует HTTP-запрос в объект аутентификации.
   *
   * @param httpRequest HTTP-запрос, который содержит информацию для аутентификации
   * @return объект PreAuthenticatedAuthenticationToken, представляющий аутентификацию пользователя
   * @throws BadCredentialsException если аутентификация не удалась из-за недопустимых учетных данных
   */
  PreAuthenticatedAuthenticationToken convert(HttpServletRequest httpRequest) throws BadCredentialsException;
}

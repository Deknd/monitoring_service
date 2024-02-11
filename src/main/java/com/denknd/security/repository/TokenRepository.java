package com.denknd.security.repository;

import com.denknd.security.entity.TokenBlock;

import java.sql.SQLException;
/**
 * Интерфейс репозитория для работы с токенами в базе данных.
 */
public interface TokenRepository {
  /**
   * Сохраняет токен в базу данных.
   *
   * @param tokenBlock Токен для сохранения.
   * @return Сохраненный токен.
   * @throws SQLException если возникает ошибка при выполнении SQL-запроса.
   */
  TokenBlock save(TokenBlock tokenBlock) throws SQLException;
  /**
   * Проверяет существование токена по его идентификатору.
   *
   * @param tokenId Идентификатор токена.
   * @return true, если токен существует, в противном случае - false.
   * @throws SQLException если возникает ошибка при выполнении SQL-запроса.
   */
  boolean existsByTokenId(String tokenId) throws SQLException ;
}

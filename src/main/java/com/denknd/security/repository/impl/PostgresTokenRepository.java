package com.denknd.security.repository.impl;

import com.denknd.security.entity.TokenBlock;
import com.denknd.security.repository.TokenRepository;
import com.denknd.util.DataBaseConnection;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 * Реализация интерфейса для хранения объектов пользователя в БД.
 */
@RequiredArgsConstructor
public class PostgresTokenRepository implements TokenRepository {
  /**
   * Выдает соединение с базой данных
   */
  private final DataBaseConnection dataBaseConnection;

  /**
   * Сохраняет токен в базу данных.
   *
   * @param tokenBlock Токен для сохранения.
   * @return Сохраненный токен.
   */
  @Override
  public TokenBlock save(TokenBlock tokenBlock) throws SQLException {
    var sql = "INSERT INTO token_block (token_id, expiration_time) VALUES (?, ?)";
    var connection = this.dataBaseConnection.createConnection();

    connection.setAutoCommit(false);
    try (var preparedStatement = connection.prepareStatement(sql,
            Statement.RETURN_GENERATED_KEYS)) {
      preparedStatement.setString(1, tokenBlock.tokenId());
      preparedStatement.setObject(2, tokenBlock.expirationTime(), Types.TIMESTAMP_WITH_TIMEZONE);
      var affectedRows = preparedStatement.executeUpdate();
      if (affectedRows == 0) {
        connection.rollback();
        throw new SQLException("Ошибка блокировки токена, ни одной строки не добавлено в БД");
      }
      try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          var keysLong = generatedKeys.getLong(1);
          connection.commit();
          return new TokenBlock(keysLong, tokenBlock.tokenId(), tokenBlock.expirationTime());
        } else {
          connection.rollback();
          throw new SQLException("Токен не сохранен, не сгенерирован идентификатор");
        }
      }
    } finally {
      if (connection != null) {
        connection.close();
      }
    }
  }

  /**
   * Проверяет существование токена по его идентификатору.
   *
   * @param tokenId Идентификатор токена.
   * @return true, если токен существует, в противном случае - false.
   */
  @Override
  public boolean existsByTokenId(String tokenId) throws SQLException {
    var sql = "SELECT 1 FROM token_block WHERE token_id = ?";
    try (var connection = dataBaseConnection.createConnection();
         var preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, tokenId);
      try (var resultSet = preparedStatement.executeQuery()) {
        return resultSet.next();
      }
    }
  }
}

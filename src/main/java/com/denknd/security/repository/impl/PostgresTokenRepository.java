package com.denknd.security.repository.impl;

import com.denknd.security.entity.TokenBlock;
import com.denknd.security.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 * Реализация интерфейса для хранения объектов пользователя в БД.
 */
@Repository
@RequiredArgsConstructor
public class PostgresTokenRepository implements TokenRepository {
  private final JdbcTemplate jdbcTemplate;

  /**
   * Сохраняет токен в базу данных.
   *
   * @param tokenBlock Токен для сохранения.
   * @return Сохраненный токен.
   */
  @Override
  public TokenBlock save(TokenBlock tokenBlock) throws SQLException {
    var sql = "INSERT INTO token_block (token_id, expiration_time) VALUES (?, ?)";
    var keyHolder = new GeneratedKeyHolder();
    var affectedRows = jdbcTemplate.update(con -> {
      var preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, tokenBlock.tokenId());
      preparedStatement.setObject(2, tokenBlock.expirationTime(), Types.TIMESTAMP_WITH_TIMEZONE);
      return preparedStatement;
    }, keyHolder);
    if (affectedRows == 0) {
      throw new SQLException("Ошибка блокировки токена, ни одной строки не добавлено в БД");
    }

    var generatedKeys = keyHolder.getKeys();
    if (generatedKeys == null || generatedKeys.size() == 0 || generatedKeys.get("token_block_id") == null) {
      throw new SQLException("Ошибка сохранения токена, идентификатор не сгенерирован");
    }
    var generatedId = (Long) generatedKeys.get("token_block_id");
    return new TokenBlock(generatedId, tokenBlock.tokenId(), tokenBlock.expirationTime());
  }

  /**
   * Проверяет существование токена по его идентификатору.
   *
   * @param tokenId Идентификатор токена.
   * @return true, если токен существует, в противном случае - false.
   */
  @Override
  public boolean existsByTokenId(String tokenId) {
    var sql = "SELECT COUNT(*) FROM token_block WHERE token_id = ?";
    var count = jdbcTemplate.queryForObject(sql, Integer.class, tokenId);
    return count!= null && count > 0;
  }
}
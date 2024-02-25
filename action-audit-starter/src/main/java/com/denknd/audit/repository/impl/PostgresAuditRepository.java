package com.denknd.audit.repository.impl;

import com.denknd.audit.entity.Audit;
import com.denknd.audit.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 * Реализация интерфейса {@link AuditRepository}, предназначенная для хранения объектов аудита в базе данных Postgres.
 */
@RequiredArgsConstructor
public class PostgresAuditRepository implements AuditRepository {
  private final JdbcTemplate jdbcTemplate;

  /**
   * Сохраняет объект аудита в базу данных Postgres.
   *
   * @param audit объект аудита для сохранения
   * @return сохраненный объект аудита с присвоенным идентификатором
   * @throws SQLException выбрасывается в случае ошибки SQL
   */
  @Transactional
  @Override
  public Audit save(Audit audit) throws SQLException {
    if (audit.getAuditId() != null) {
      throw new SQLException("Ошибка сохранения, нельзя сохранять аудит с установленным айди");
    }

    var sql = "INSERT INTO audits (operation, operation_time, user_id) VALUES (?, ?, ?)";

    var keyHolder = new GeneratedKeyHolder();

    int affectedRows = jdbcTemplate.update(con -> {
      var preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setObject(1, audit.getOperation(), Types.VARCHAR);
      preparedStatement.setObject(2, audit.getOperationTime(), Types.TIMESTAMP_WITH_TIMEZONE);
      preparedStatement.setLong(3, audit.getUserId());
      return preparedStatement;
    }, keyHolder);

    if (affectedRows == 0) {
      throw new SQLException("Ошибка сохранения, не добавлено ни одной строки");
    }

    var generatedKeys = keyHolder.getKeys();
    if (generatedKeys == null || generatedKeys.size() == 0 || generatedKeys.get("audit_id") == null) {
      throw new SQLException("Ошибка сохранения аудита, идентификатор не сгенерирован");
    }
    var generatedId = (Long) generatedKeys.get("audit_id");
    audit.setAuditId(generatedId);

    return audit;
  }
}
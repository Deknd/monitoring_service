package com.denknd.out.audit;

import com.denknd.repository.AuditRepository;
import com.denknd.util.DataBaseConnection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 * Реализация интерфейса {@link AuditRepository}, предназначенная для хранения объектов аудита в базе данных Postgres.
 */
@RequiredArgsConstructor
@Repository
public class PostgresAuditRepository implements AuditRepository {
  /**
   * Соединение с базой данных.
   */
  private final DataBaseConnection dataBaseConnection;

  /**
   * Сохраняет объект аудита в базу данных Postgres.
   *
   * @param audit объект аудита для сохранения
   * @return сохраненный объект аудита с присвоенным идентификатором
   * @throws SQLException выбрасывается в случае ошибки SQL
   */
  @Override
  public Audit save(Audit audit) throws SQLException {
    if (audit.getAuditId() != null) {
      throw new SQLException("Ошибка сохранения, нельзя сохранять аудит с установленным айди");
    }
    var sql = "INSERT INTO audits (operation, operation_time, user_id) VALUES (?, ?, ?)";
    var connection = dataBaseConnection.createConnection();
    connection.setAutoCommit(false);
    try (
            var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
    ) {

      preparedStatement.setObject(1, audit.getOperation(), Types.VARCHAR);
      preparedStatement.setObject(2, audit.getOperationTime(), Types.TIMESTAMP_WITH_TIMEZONE);
      preparedStatement.setLong(3, audit.getUser().userId());

      int affectedRows = preparedStatement.executeUpdate();

      if (affectedRows == 0) {
        connection.rollback();
        throw new SQLException("Ошибка сохранения, не добавлено ни одной строки");
      }

      try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          audit.setAuditId(generatedKeys.getLong(1));
          connection.commit();
          return audit;
        } else {
          connection.rollback();
          throw new SQLException("Ошибка сохранения, айди не установлено");
        }
      }
    } finally {
      if (connection != null) {
        connection.close();
      }
    }
  }
}
package com.denknd.repository.impl;

import com.denknd.entity.Meter;
import com.denknd.repository.MeterCountRepository;
import com.denknd.util.DataBaseConnection;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 * Реализация интерфейса для хранения информации о счетчиках в БД.
 */
@RequiredArgsConstructor
public class PostgresMeterCountRepository implements MeterCountRepository {

  /**
   * Выдает соединение с базой данных
   */
  private final DataBaseConnection dataBaseConnection;

  /**
   * Добавляет новый счетчик в хранилище.
   *
   * @param meter Полностью заполненный, за исключением meterCountId.
   *              Если передан с заполненным meterCountId, сохранение не произойдет.
   * @return Полностью заполненный объект Meter
   * @throws SQLException выкидывается, если не соблюдены ограничения БД
   */
  @Override
  public Meter save(Meter meter) throws SQLException {
    if (meter.getMeterCountId() != null) {
      throw new SQLException("Ошибка сохранения нового счетчика. Переданный объект счетчика содержит идентификатор");
    }
    var sql = "INSERT INTO meters (address_id, type_meter_id, registration_date) VALUES (?, ?, ?)";
    var connection = dataBaseConnection.createConnection();
    connection.setAutoCommit(false);
    try (var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      preparedStatement.setObject(1, meter.getAddressId(), Types.BIGINT);
      preparedStatement.setObject(2, meter.getTypeMeterId(), Types.BIGINT);
      preparedStatement.setObject(3, meter.getRegistrationDate(), Types.TIMESTAMP_WITH_TIMEZONE);
      int affectedRows = preparedStatement.executeUpdate();
      if (affectedRows == 0) {
        connection.rollback();
        throw new SQLException("Ошибка сохранения информации о счетчике, ни одной строки не добавлено");
      }
      try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          meter.setMeterCountId(generatedKeys.getLong(1));
          connection.commit();
          return meter;
        } else {
          connection.rollback();
          throw new SQLException("Ошибка сохранения информации о счетчике, не сгенерировано идентификатора");
        }
      }
    } finally {
      if (connection != null) {
        connection.close();
      }
    }
  }

  /**
   * Добавляет полученные данные в Бд
   *
   * @param meter информация о счетчике, заполненная
   * @return возвращает объект с данными о счетчике
   * @throws SQLException в случае ошибки обновления бд
   */
  @Override
  public Meter update(Meter meter) throws SQLException {
    if (meter.getAddressId() == null || meter.getTypeMeterId() == null) {
      throw new SQLException("Ошибка добавления информации, идентификаторы не определены:"
              + " addressId: " + meter.getAddressId()
              + ", typeMeterId: " + meter.getTypeMeterId());
    }
    String sql = "UPDATE meters " +
            "SET serial_number = ?, " +
            "last_check_date = ?, " +
            "meter_model = ? " +
            "WHERE address_id = ? AND type_meter_id = ?";
    var connection = dataBaseConnection.createConnection();
    connection.setAutoCommit(false);
    try (var stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, meter.getSerialNumber());
      stmt.setObject(2, meter.getLastCheckDate(), Types.TIMESTAMP_WITH_TIMEZONE);
      stmt.setString(3, meter.getMeterModel());
      stmt.setLong(4, meter.getAddressId());
      stmt.setLong(5, meter.getTypeMeterId());
      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected == 0) {
        connection.rollback();
        throw new SQLException("Ошибка сохранения информации о счетчике, ни одной строки не добавлено");
      }
      return meter;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new SQLException("Ошибка сохранения информации о счетчике, данные не добавлены");
    } finally {
      if (connection != null) {
        connection.close();
      }
    }
  }
}

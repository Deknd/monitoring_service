package com.denknd.repository.impl;

import com.denknd.entity.Meter;
import com.denknd.repository.MeterCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 * Реализация интерфейса для хранения информации о счетчиках в БД.
 */
@Repository
@RequiredArgsConstructor
public class PostgresMeterCountRepository implements MeterCountRepository {

  private final JdbcTemplate jdbcTemplate;

  /**
   * Добавляет новый счетчик в хранилище.
   *
   * @param meter Полностью заполненный, за исключением meterCountId.
   *              Если передан с заполненным meterCountId, сохранение не произойдет.
   * @return Полностью заполненный объект Meter
   * @throws SQLException выкидывается, если не соблюдены ограничения БД
   */
  @Transactional
  @Override
  public Meter save(Meter meter) throws SQLException {
    if (meter.getMeterCountId() != null) {
      throw new SQLException("Ошибка сохранения нового счетчика. Переданный объект счетчика содержит идентификатор");
    }
    var sql = "INSERT INTO meters (address_id, type_meter_id, registration_date) VALUES (?, ?, ?)";
    var keyHolder = new GeneratedKeyHolder();
    var affectedRows = jdbcTemplate.update(connection -> {
      var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setObject(1, meter.getAddressId(), Types.BIGINT);
      preparedStatement.setObject(2, meter.getTypeMeterId(), Types.BIGINT);
      preparedStatement.setObject(3, meter.getRegistrationDate(), Types.TIMESTAMP_WITH_TIMEZONE);
      return preparedStatement;
    }, keyHolder);
    if (affectedRows == 0) {
      throw new SQLException("Ошибка сохранения информации о счетчике, ни одной строки не добавлено");
    }
    var generatedKeys = keyHolder.getKeys();
    if (generatedKeys == null || generatedKeys.size() == 0 || generatedKeys.get("meter_count_id") == null) {
      throw new SQLException("Ошибка сохранения информации о счетчике, идентификатор не сгенерирован");
    }
    var generatedId = (Long) generatedKeys.get("meter_count_id");
    meter.setMeterCountId(generatedId);
    return meter;
  }

  /**
   * Добавляет полученные данные в Бд
   *
   * @param meter информация о счетчике, заполненная
   * @return возвращает объект с данными о счетчике
   * @throws SQLException в случае ошибки обновления бд
   */
  @Transactional
  @Override
  public Meter update(Meter meter) throws SQLException {
    if (meter.getAddressId() == null || meter.getTypeMeterId() == null) {
      throw new SQLException("Ошибка добавления информации, идентификаторы не определены:"
              + " addressId: " + meter.getAddressId()
              + ", typeMeterId: " + meter.getTypeMeterId());
    }
    var sql = "UPDATE meters " +
            "SET serial_number = ?, " +
            "last_check_date = ?, " +
            "meter_model = ? " +
            "WHERE address_id = ? AND type_meter_id = ?";
    var rowsAffected = jdbcTemplate.update(sql, preparedStatement -> {
      preparedStatement.setString(1, meter.getSerialNumber());
      preparedStatement.setObject(2, meter.getLastCheckDate(), Types.TIMESTAMP_WITH_TIMEZONE);
      preparedStatement.setString(3, meter.getMeterModel());
      preparedStatement.setLong(4, meter.getAddressId());
      preparedStatement.setLong(5, meter.getTypeMeterId());
    });
    if (rowsAffected == 0) {
      throw new SQLException("Ошибка сохранения информации о счетчике, ни одной строки не добавлено");
    }
    return meter;
  }
}
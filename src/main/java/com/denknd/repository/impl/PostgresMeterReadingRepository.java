package com.denknd.repository.impl;

import com.denknd.entity.MeterReading;
import com.denknd.mappers.MeterReadingMapper;
import com.denknd.repository.MeterReadingRepository;
import com.denknd.util.DataBaseConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса для хранения показаний в БД.
 */
@RequiredArgsConstructor
@Log4j2
public class PostgresMeterReadingRepository implements MeterReadingRepository {
  /**
   * Выдает соединение с базой данных
   */
  private final DataBaseConnection dataBaseConnection;
  /**
   * Маппер для мапинга показаний
   */
  private final MeterReadingMapper meterReadingMapper;

  /**
   * Сохраняет показания в памяти.
   *
   * @param meterReading Полностью заполненный объект показаний (без meterId).
   * @return Возвращает объект с сгенерированным meterId.
   */
  @Override
  public MeterReading save(MeterReading meterReading) throws SQLException {
    if (meterReading.getMeterId() != null) {
      throw new SQLException("Ошибка сохранения новых показаний. Переданные показания уже содержат идентификатор");
    }
    var sql = "INSERT INTO meter_readings (address_id, type_meter_id, meter_value, submission_month, time_send_meter) VALUES (?, ?, ?, ?, ?)";
    var connection = dataBaseConnection.createConnection();
    connection.setAutoCommit(false);
    try (var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      preparedStatement.setLong(1, meterReading.getAddress().getAddressId());
      preparedStatement.setLong(2, meterReading.getTypeMeter().getTypeMeterId());
      preparedStatement.setDouble(3, meterReading.getMeterValue());
      preparedStatement.setString(4, meterReading.getSubmissionMonth().format(DateTimeFormatter.ofPattern("yyyy-MM")));
      preparedStatement.setObject(5, meterReading.getTimeSendMeter(), Types.TIMESTAMP_WITH_TIMEZONE);
      var affectedRows = preparedStatement.executeUpdate();
      if (affectedRows == 0) {
        connection.rollback();
        throw new SQLException("Ошибка сохранения показаний, ни одной строки не добавлено");
      }
      try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          meterReading.setMeterId(generatedKeys.getLong(1));
          connection.commit();
          return meterReading;
        } else {
          connection.rollback();
          throw new SQLException("Ошибка сохранения показаний, не сгенерирован идентификатор");
        }
      }
    } finally {
      if (connection != null) {
        connection.close();
      }
    }
  }

  /**
   * Получает список показаний по идентификатору адреса.
   *
   * @param addressId Идентификатор адреса.
   * @return Список показаний по этому адресу.
   */
  @Override
  public List<MeterReading> findMeterReadingByAddressId(Long addressId) {
    var sql = "SELECT * FROM meter_readings WHERE address_id = ?";

    try (var connection = dataBaseConnection.createConnection();
         var preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setLong(1, addressId);
      try (var resultSet = preparedStatement.executeQuery()) {
        var meterReadings = new ArrayList<MeterReading>();
        while (resultSet.next()) {
          var meterReading = this.meterReadingMapper.mapResultSetToMeterReading(resultSet);
          meterReadings.add(meterReading);
        }
        return meterReadings;
      }
    } catch (SQLException e) {
      log.error(e.getMessage());
      return Collections.emptyList();
    }
  }

  /**
   * Получает актуальные показания по адресу и типу показаний.
   *
   * @param addressId   Идентификатор адреса, по которому нужно получить показания.
   * @param typeMeterId Идентификатор типа показаний.
   * @return Optional с актуальным типом или пустой, если показаний не найдено.
   */
  @Override
  public Optional<MeterReading> findActualMeterReading(Long addressId, Long typeMeterId) {
    var sql = "SELECT * FROM meter_readings WHERE address_id = ? AND type_meter_id = ? ORDER BY submission_month DESC LIMIT 1";
    try (var connection = dataBaseConnection.createConnection();
         var preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setLong(1, addressId);
      preparedStatement.setLong(2, typeMeterId);
      try (var resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          var meterReading = this.meterReadingMapper.mapResultSetToMeterReading(resultSet);
          return Optional.of(meterReading);
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      log.error(e.getMessage());
      return Optional.empty();
    }
  }

  /**
   * Получает актуальные показания по адресу, типу показаний и указанной дате.
   *
   * @param addressId   Идентификатор адреса, по которому нужно получить показания.
   * @param typeMeterId Идентификатор типа показаний.
   * @param date        Дата, по которой нужны показания.
   * @return Optional с актуальным типом или пустой, если показаний не найдено.
   */
  @Override
  public Optional<MeterReading> findMeterReadingForDate(Long addressId, Long typeMeterId, YearMonth date) {
    var sql = "SELECT * FROM meter_readings WHERE address_id = ?";
    var params = new ArrayList<Object>();

    params.add(addressId);

    if (typeMeterId != null) {
      sql += " AND type_meter_id = ?";
      params.add(typeMeterId);
    }

    if (date != null) {
      sql += " AND submission_month = ?";
      params.add(date.format(DateTimeFormatter.ofPattern("yyyy-MM")));
    }

    sql += " LIMIT 1";

    try (var connection = dataBaseConnection.createConnection();
         var preparedStatement = connection.prepareStatement(sql)) {

      for (int i = 0; i < params.size(); i++) {
        preparedStatement.setObject(i + 1, params.get(i));
      }

      try (var resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          var meterReading = this.meterReadingMapper.mapResultSetToMeterReading(resultSet);
          return Optional.of(meterReading);
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error while searching for meter readings for the specified month", e);
    }
  }

}

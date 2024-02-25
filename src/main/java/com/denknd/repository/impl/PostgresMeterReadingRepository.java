package com.denknd.repository.impl;

import com.denknd.entity.MeterReading;
import com.denknd.mappers.MeterReadingMapper;
import com.denknd.repository.MeterReadingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса для хранения показаний в БД.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class PostgresMeterReadingRepository implements MeterReadingRepository {
  private final JdbcTemplate jdbcTemplate;
  private final MeterReadingMapper meterReadingMapper;

  /**
   * Сохраняет показания в памяти.
   *
   * @param meterReading Полностью заполненный объект показаний (без meterId).
   * @return Возвращает объект с сгенерированным meterId.
   */
  @Transactional
  @Override
  public MeterReading save(MeterReading meterReading) throws SQLException {
    if (meterReading.getMeterId() != null) {
      throw new SQLException("Ошибка сохранения новых показаний. Переданные показания уже содержат идентификатор");
    }
    var sql = "INSERT INTO meter_readings (address_id, type_meter_id, meter_value, submission_month, time_send_meter) VALUES (?, ?, ?, ?, ?)";
    var keyHolder = new GeneratedKeyHolder();
    var affectedRows = jdbcTemplate.update(con -> {
      var preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setLong(1, meterReading.getAddress().getAddressId());
      preparedStatement.setLong(2, meterReading.getTypeMeter().getTypeMeterId());
      preparedStatement.setDouble(3, meterReading.getMeterValue());
      preparedStatement.setString(4, meterReading.getSubmissionMonth().format(DateTimeFormatter.ofPattern("yyyy-MM")));
      preparedStatement.setObject(5, meterReading.getTimeSendMeter(), Types.TIMESTAMP_WITH_TIMEZONE);
      return preparedStatement;
    }, keyHolder);
    if (affectedRows == 0) {
      throw new SQLException("Ошибка сохранения показаний, ни одной строки не добавлено");
    }
    var generatedKeys = keyHolder.getKeys();
    if (generatedKeys == null || generatedKeys.size() == 0 || generatedKeys.get("meter_id") == null) {
      throw new SQLException("Ошибка сохранения показаний, идентификатор не сгенерирован");
    }
    var generatedId = (Long) generatedKeys.get("meter_id");
    meterReading.setMeterId(generatedId);
    return meterReading;
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
    RowMapper<MeterReading> rowMapper = (resultSet, rowNum) -> this.meterReadingMapper.mapResultSetToMeterReading(resultSet);
    return jdbcTemplate.query(sql, rowMapper, addressId);
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
    String sql = "SELECT * FROM meter_readings WHERE address_id = ? AND type_meter_id = ? ORDER BY submission_month DESC LIMIT 1";
    RowMapper<MeterReading> rowMapper = (resultSet, rowNum) -> this.meterReadingMapper.mapResultSetToMeterReading(resultSet);
    List<MeterReading> meterReadings = jdbcTemplate.query(sql, rowMapper, addressId, typeMeterId);
    if (!meterReadings.isEmpty()) {
      return Optional.ofNullable(meterReadings.get(0));
    } else {
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
    StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM meter_readings WHERE address_id = ?");
    List<Object> params = new ArrayList<>();
    params.add(addressId);
    if (typeMeterId != null) {
      sqlBuilder.append(" AND type_meter_id = ?");
      params.add(typeMeterId);
    }
    if (date != null) {
      sqlBuilder.append(" AND submission_month = ?");
      params.add(date.format(DateTimeFormatter.ofPattern("yyyy-MM")));
    }
    sqlBuilder.append(" LIMIT 1");
    String sql = sqlBuilder.toString();
    RowMapper<MeterReading> rowMapper = (resultSet, rowNum) -> this.meterReadingMapper.mapResultSetToMeterReading(resultSet);
    List<MeterReading> meterReadings = jdbcTemplate.query(sql, rowMapper, params.toArray());
    return meterReadings.isEmpty() ? Optional.empty() : Optional.of(meterReadings.get(0));
  }
}
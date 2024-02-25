package com.denknd.repository.impl;

import com.denknd.entity.TypeMeter;
import com.denknd.mappers.TypeMeterMapper;
import com.denknd.repository.TypeMeterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация интерфейса для хранения типов показаний в базу данных.
 */
@Repository
@RequiredArgsConstructor
public class PostgresTypeMeterRepository implements TypeMeterRepository {
  private final JdbcTemplate jdbcTemplate;
  private final TypeMeterMapper typeMeterMapper;

  /**
   * Ищет все доступные типы показаний.
   *
   * @return Список доступных типов показаний.
   */
  @Override
  public List<TypeMeter> findTypeMeter() {
    var sql = "SELECT * FROM type_meters";
    RowMapper<TypeMeter> rowMapper = (resultSet, rowNum) -> this.typeMeterMapper.mapResultSetToTypeMeter(resultSet);
    return jdbcTemplate.query(sql, rowMapper);
  }

  /**
   * Сохраняет новые типы показаний в базу данных.
   *
   * @param typeMeter Полностью заполненный объект без идентификатора.
   * @return Полностью заполненный объект с идентификатором.
   * @throws SQLException возникает, когда данный не совпадают с ограничениями базы данных
   */
  @Transactional
  @Override
  public TypeMeter save(TypeMeter typeMeter) throws SQLException {
    var sql = "INSERT INTO type_meters (type_code, type_description, metric) VALUES (?, ?, ?)";
    if (typeMeter.getTypeMeterId() != null) {
      throw new SQLException("Попытка сохранить сущность с идентификатором");
    }
    var keyHolder = new GeneratedKeyHolder();
    var affectedRows = jdbcTemplate.update(con -> {
      var preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, typeMeter.getTypeCode());
      preparedStatement.setString(2, typeMeter.getTypeDescription());
      preparedStatement.setString(3, typeMeter.getMetric());
      return preparedStatement;
    }, keyHolder);
    if (affectedRows == 0) {
      throw new SQLException("Создание TypeMeter не удалось.");
    }
    var generatedKeys = keyHolder.getKeys();
    if (generatedKeys == null || generatedKeys.size() == 0 || generatedKeys.get("type_meter_id") == null) {
      throw new SQLException("Ошибка сохранения типа показаний, идентификатор не сгенерирован");
    }
    var generatedId = (Long) generatedKeys.get("type_meter_id");
    typeMeter.setTypeMeterId(generatedId);
    return typeMeter;
  }

}
package com.denknd.repository.impl;

import com.denknd.entity.TypeMeter;
import com.denknd.repository.TypeMeterRepository;
import com.denknd.util.DataBaseConnection;
import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация интерфейса для хранения типов показаний в базу данных.
 */
@RequiredArgsConstructor
public class PostgresTypeMeterRepository implements TypeMeterRepository {
  /**
   * Выдает соединение с базой данных
   */
  private final DataBaseConnection dataBaseConnection;

  /**
   * Ищет все доступные типы показаний.
   *
   * @return Список доступных типов показаний.
   */
  @Override
  public List<TypeMeter> findTypeMeter() {
    var typeMeters = new ArrayList<TypeMeter>();
    var sql = "SELECT * FROM type_meters";
    try (
            var connection = dataBaseConnection.createConnection();
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery(sql);
    ) {

      while (resultSet.next()) {
        var typeMeter = mapResultSetToTypeMeter(resultSet);
        typeMeters.add(typeMeter);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return typeMeters;
  }

  /**
   * Сохраняет новые типы показаний в базу данных.
   *
   * @param typeMeter Полностью заполненный объект без идентификатора.
   * @return Полностью заполненный объект с идентификатором.
   * @throws SQLException возникает, когда данный не совпадают с ограничениями базы данных
   */
  @Override
  public TypeMeter save(TypeMeter typeMeter) throws SQLException {
    var sql = "INSERT INTO type_meters (type_code, type_description, metric) VALUES (?, ?, ?)";
    if (typeMeter.getTypeMeterId() != null) {
      throw new SQLException("Попытка сохранить сущность с идентификатором");
    }
    var connection = dataBaseConnection.createConnection();
    connection.setAutoCommit(false);
    try (
            var preparedStatement = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS)
    ) {
      preparedStatement.setString(1, typeMeter.getTypeCode());
      preparedStatement.setString(2, typeMeter.getTypeDescription());
      preparedStatement.setString(3, typeMeter.getMetric());
      var affectedRows = preparedStatement.executeUpdate();
      if (affectedRows == 0) {
        connection.rollback();
        throw new SQLException("Создание TypeMeter не удалось.");
      }
      try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          typeMeter.setTypeMeterId(generatedKeys.getLong(1));
          connection.commit();
          return typeMeter;
        } else {
          connection.rollback();
          throw new SQLException("Создание TypeMeter не удалось, идентификатор не получен");
        }
      }
    } finally {
      if (connection != null) {
        connection.close();
      }
    }

  }

  /**
   * Создает объект TypeMeter
   *
   * @param resultSet данные из базы данных
   * @return заполненный объект
   * @throws SQLException не верные данные из Бд
   */
  private TypeMeter mapResultSetToTypeMeter(ResultSet resultSet) throws SQLException {
    return TypeMeter.builder()
            .typeMeterId(resultSet.getLong("type_meter_id"))
            .typeCode(resultSet.getString("type_code"))
            .typeDescription(resultSet.getString("type_description"))
            .metric(resultSet.getString("metric"))
            .build();
  }
}

package com.denknd.repository.impl;

import com.denknd.entity.User;
import com.denknd.mappers.UserMapper;
import com.denknd.repository.UserRepository;
import com.denknd.util.DataBaseConnection;
import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
/**
 * Реализация интерфейса для хранения объектов пользователя в БД.
 */
@RequiredArgsConstructor
public class PostgresUserRepository implements UserRepository {
  /**
   * Выдает соединение с базой данных
   */
  private final DataBaseConnection dataBaseConnection;
  /**
   * Маппер для объектов пользователя.
   */
  private final UserMapper userMapper;
  /**
   * Проверяет, существует ли пользователь с указанным email.
   *
   * @param email Электронная почта для проверки.
   * @return true, если пользователь существует.
   */
  @Override
  public boolean existUser(String email) {
    var sql = "SELECT 1 FROM users WHERE email = ?";
    try (
            var connection = dataBaseConnection.createConnection();
            var preparedStatement = connection.prepareStatement(sql);
    ) {
      preparedStatement.setString(1, email);
      try (var resultSet = preparedStatement.executeQuery()) {
        return resultSet.next();
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
  /**
   * Проверяет, существует ли пользователь с указанным айди.
   *
   * @param userId Айди для проверки.
   * @return true, если пользователь существует.
   */
  @Override
  public boolean existUserByUserId(Long userId) {
    var sql = "SELECT 1 FROM users WHERE user_id = ?";
    try (
            var connection = dataBaseConnection.createConnection();
            var preparedStatement = connection.prepareStatement(sql)
    ) {
      preparedStatement.setLong(1, userId);
      try (var resultSet = preparedStatement.executeQuery()) {
        return resultSet.next();
      }

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
  /**
   * Сохраняет пользователя в память.
   *
   * @param user Заполненный объект пользователя, без айди.
   * @return Возвращает копию сохраненного пользователя.
   */
  @Override
  public User save(User user) throws SQLException {
    if (user.getUserId() != null) {
      throw new SQLException("Попытка сохранить пользователя у которого уже есть идентификатор");
    }
    var sql = "INSERT INTO users (email, roles, password, user_name, user_last_name) VALUES (?, ?, ?, ?, ?)";
    var connection = this.dataBaseConnection.createConnection();
    connection.setAutoCommit(false);
    try (
            var preparedStatement = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS)
    ) {

      preparedStatement.setString(1, user.getEmail());
      preparedStatement.setString(2, user.getRole().toString());
      preparedStatement.setString(3, user.getPassword());
      preparedStatement.setString(4, user.getFirstName());
      preparedStatement.setString(5, user.getLastName());

      var affectedRows = preparedStatement.executeUpdate();

      if (affectedRows == 0) {
        connection.rollback();
        throw new SQLException("Пользователь не создан, ни одной строки не добавлено в БД");
      }

      try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          user.setUserId(generatedKeys.getLong(1));
          connection.commit();
          return user;
        } else {
          connection.rollback();
          throw new SQLException("Пользователь не создан, не сгенерирован идентификатор");
        }
      }
    } finally {
      if(connection != null){
        connection.close();
      }
    }
  }
  /**
   * Ищет пользователя по email.
   *
   * @param email Email, по которому нужно найти пользователя.
   * @return Optional с пользователем или пустой Optional.
   */
  @Override
  public Optional<User> find(String email) {
    var sql = "SELECT * FROM users WHERE email = ?";
    try (
            var connection = dataBaseConnection.createConnection();
            var preparedStatement = connection.prepareStatement(sql)
    ) {
      preparedStatement.setString(1, email);
      try (var resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {

          var user = this.userMapper.mapResultSetToUser(resultSet);
          return Optional.of(user);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }
  /**
   * Ищет пользователя по айди.
   *
   * @param id Айди, по которому нужно найти пользователя.
   * @return Optional с пользователем или пустой Optional.
   */
  @Override
  public Optional<User> findById(Long id) {
    var sql = "SELECT * FROM users WHERE user_id = ?";
    try (
            var connection = dataBaseConnection.createConnection();
            var preparedStatement = connection.prepareStatement(sql)
    ) {
      preparedStatement.setLong(1, id);
      try (var resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          return Optional.of(this.userMapper.mapResultSetToUser(resultSet));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }
}

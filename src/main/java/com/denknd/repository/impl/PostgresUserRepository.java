package com.denknd.repository.impl;

import com.denknd.entity.User;
import com.denknd.mappers.UserMapper;
import com.denknd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/**
 * Реализация интерфейса для хранения объектов пользователя в БД.
 */
@Repository
@RequiredArgsConstructor
public class PostgresUserRepository implements UserRepository {
  /**
   * Выдает соединение с базой данных
   */
  private final JdbcTemplate jdbcTemplate;
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
    return jdbcTemplate.queryForObject(sql, Boolean.class, email) != null;
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
    return jdbcTemplate.queryForObject(sql, Boolean.class, userId) != null;
  }


  /**
   * Сохраняет пользователя в память.
   *
   * @param user Заполненный объект пользователя, без айди.
   * @return Возвращает копию сохраненного пользователя.
   */
  @Transactional
  @Override
  public User save(User user) throws SQLException {
    if (user.getUserId() != null) {
      throw new SQLException("Попытка сохранить пользователя у которого уже есть идентификатор");
    }
    var sql = "INSERT INTO users (email, roles, password, user_name, user_last_name) VALUES (?, ?, ?, ?, ?)";
    var keyHolder = new GeneratedKeyHolder();
    var affectedRows = jdbcTemplate.update(con -> {
      var preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, user.getEmail());
      preparedStatement.setString(2, user.getRole().toString());
      preparedStatement.setString(3, user.getPassword());
      preparedStatement.setString(4, user.getFirstName());
      preparedStatement.setString(5, user.getLastName());
      return preparedStatement;
    }, keyHolder);
    if (affectedRows == 0) {
      throw new SQLException("Пользователь не создан, ни одной строки не добавлено в БД");
    }
    var generatedKeys = keyHolder.getKeys();
    if (generatedKeys == null || generatedKeys.size() == 0 || generatedKeys.get("user_id") == null) {
      throw new SQLException("Ошибка сохранения пользователя, идентификатор не сгенерирован");
    }
    var generatedId = (Long) generatedKeys.get("user_id");
    user.setUserId(generatedId);
    return user;
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
    RowMapper<User> rowMapper = (resultSet, rowNum) -> this.userMapper.mapResultSetToUser(resultSet);
    var users = jdbcTemplate.query(sql, rowMapper, email);
    return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
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
    RowMapper<User> rowMapper = (resultSet, rowNum) -> this.userMapper.mapResultSetToUser(resultSet);
    var users = jdbcTemplate.query(sql, rowMapper, id);
    return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
  }
}
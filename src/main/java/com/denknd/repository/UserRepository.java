package com.denknd.repository;

import com.denknd.entity.User;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Интерфейс репозитория для работы с пользователями.
 */
public interface UserRepository {

  /**
   * Проверяет, существует ли пользователь с указанным электронным адресом.
   *
   * @param email Электронный адрес пользователя.
   * @return true, если пользователь существует, иначе false.
   */
  boolean existUser(String email);

  /**
   * Проверяет, существует ли пользователь с указанным идентификатором.
   *
   * @param userId Идентификатор пользователя.
   * @return true, если пользователь существует, иначе false.
   */
  boolean existUserByUserId(Long userId);

  /**
   * Сохраняет пользователя в базу данных.
   *
   * @param user Полностью заполненный объект пользователя, без идентификатора.
   * @return Полностью заполненный объект пользователя с присвоенным идентификатором.
   */
  User save(User user) throws SQLException;

  /**
   * Ищет пользователя по электронному адресу.
   *
   * @param email Электронный адрес пользователя.
   * @return Optional с найденным пользователем или пустым, если пользователь не найден.
   */
  Optional<User> find(String email);

  /**
   * Ищет пользователя по идентификатору.
   *
   * @param id Идентификатор пользователя.
   * @return Optional с найденным пользователем или пустым, если пользователь не найден.
   */
  Optional<User> findById(Long id);
}

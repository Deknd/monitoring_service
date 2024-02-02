package com.denknd.repository.impl;

import com.denknd.entity.User;
import com.denknd.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * Реализация интерфейса для хранения объектов пользователя в памяти.
 */
public class InMemoryUserRepository implements UserRepository {
  /**
   * Хранение связи между айди и объектом пользователя.
   */
  private final Map<Long, User> userDb = new HashMap<>();
  /**
   * Хранение связи между электронной почтой и айди пользователя.
   */
  private final Map<String, Long> emailToUserIdMap = new HashMap<>();
  /**
   * Генератор случайных чисел для создания айди пользователя.
   */
  private final Random random = new Random();

  /**
   * Проверяет, существует ли пользователь с указанным email.
   *
   * @param email Электронная почта для проверки.
   * @return true, если пользователь существует.
   */
  @Override
  public boolean existUser(String email) {
    return this.emailToUserIdMap.containsKey(email);
  }

  /**
   * Проверяет, существует ли пользователь с указанным айди.
   *
   * @param userId Айди для проверки.
   * @return true, если пользователь существует.
   */
  @Override
  public boolean existUserByUserId(Long userId) {
    return this.userDb.containsKey(userId);
  }

  /**
   * Сохраняет пользователя в память.
   *
   * @param newUser Заполненный объект пользователя, без айди.
   * @return Возвращает копию сохраненного пользователя.
   */
  @Override
  public User save(User newUser) {
    long userId;
    if (newUser.getUserId() == null) {

      do {
        userId = Math.abs(this.random.nextLong());

      } while (this.userDb.containsKey(userId));
    } else {
      return null;
    }

    newUser.setUserId(userId);
    this.userDb.put(userId, newUser);
    this.emailToUserIdMap.put(newUser.getEmail(), userId);
    return buildUser(newUser);
  }

  /**
   * Копирует объект User.
   *
   * @param user Полностью собранный объект пользователя.
   * @return Копию принятого объекта.
   */
  private User buildUser(User user) {
    return User.builder()
            .userId(user.getUserId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .password(user.getPassword())
            .addresses(user.getAddresses())
            .role(user.getRole())
            .build();
  }

  /**
   * Ищет пользователя по email.
   *
   * @param email Email, по которому нужно найти пользователя.
   * @return Optional с пользователем или пустой Optional.
   */
  @Override
  public Optional<User> find(String email) {
    if (!this.emailToUserIdMap.containsKey(email)) {
      return Optional.empty();
    }
    var userId = this.emailToUserIdMap.get(email);
    return Optional.of(this.userDb.get(userId));
  }

  /**
   * Ищет пользователя по айди.
   *
   * @param id Айди, по которому нужно найти пользователя.
   * @return Optional с пользователем или пустой Optional.
   */
  @Override
  public Optional<User> findById(Long id) {
    return Optional.ofNullable(userDb.get(id));

  }
}

package com.denknd.services;

import com.denknd.entity.User;
import com.denknd.exception.UserAlreadyExistsException;

import java.security.NoSuchAlgorithmException;

/**
 * Интерфейс для работы с пользователями.
 */
public interface UserService {
  /**
   * Используется для регистрации нового пользователя.
   *
   * @param create Полностью созданный объект пользователя, без айди.
   * @return Полностью созданный объект пользователя с айди.
   * @throws UserAlreadyExistsException Если пользователь с указанным email уже существует.
   */
  User registrationUser(User create) throws UserAlreadyExistsException, NoSuchAlgorithmException;

  /**
   * Проверяет существование пользователя по его айди.
   *
   * @param userId Айди пользователя для проверки.
   * @return true, если пользователь существует.
   */
  boolean existUser(Long userId);

  /**
   * Возвращает пользователя по айди.
   *
   * @param userId Айди пользователя, которого нужно вернуть.
   * @return Объект пользователя, если существует, в противном случае null.
   */
  User getUserById(Long userId);

  /**
   * Возвращает пользователя по email.
   *
   * @param email Email пользователя, которого нужно вернуть.
   * @return Объект пользователя, если существует, в противном случае null.
   */
  User getUserByEmail(String email);

  /**
   * Проверяет существование пользователя по его email.
   *
   * @param email Email для проверки.
   * @return true, если пользователь существует.
   */
  boolean existUserByEmail(String email);

}

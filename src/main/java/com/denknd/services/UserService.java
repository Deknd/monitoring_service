package com.denknd.services;

import com.denknd.entity.Parameters;
import com.denknd.entity.User;
import com.denknd.exception.InvalidUserDataException;
import com.denknd.exception.UserAlreadyExistsException;

import java.nio.file.AccessDeniedException;
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
   * @throws NoSuchAlgorithmException   Исключение, выбрасываемое при невозможности создания хэша пароля.
   */
  User registrationUser(User create) throws NoSuchAlgorithmException;

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
   * @param parameters Параметры для получения пользователя.
   * @return Объект пользователя, если существует, в противном случае null.
   */
  User getUser(Parameters parameters);

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

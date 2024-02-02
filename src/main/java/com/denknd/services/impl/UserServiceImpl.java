package com.denknd.services.impl;

import com.denknd.entity.User;
import com.denknd.exception.UserAlreadyExistsException;
import com.denknd.repository.UserRepository;
import com.denknd.services.UserService;
import com.denknd.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;

import java.security.NoSuchAlgorithmException;

/**
 * Реализация сервиса для работы с пользователями.
 */
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  /**
   * Репозиторий для взаимодействия с хранилищем пользователей.
   */
  private final UserRepository userRepository;
  /**
   * Кодировщик и сравниватель паролей.
   */
  private final PasswordEncoder passwordEncoder;

  /**
   * Регистрирует нового пользователя.
   *
   * @param create Полностью заполненный объект пользователя без идентификатора.
   * @return Полностью заполненный объект пользователя с идентификатором.
   * @throws UserAlreadyExistsException Если пользователь с таким электронным адресом уже существует.
   */
  @Override
  public User registrationUser(User create) throws UserAlreadyExistsException, NoSuchAlgorithmException {
    if (this.userRepository.existUser(create.getEmail())) {
      throw new UserAlreadyExistsException("Данный пользователь уже существует");
    }
    create.setPassword(this.passwordEncoder.encode(create.getPassword()));
    return this.userRepository.save(create);
  }


  /**
   * Проверяет, существует ли пользователь с указанным идентификатором.
   *
   * @param userId Идентификатор пользователя.
   * @return true, если пользователь существует.
   */
  @Override
  public boolean existUser(Long userId) {
    return this.userRepository.existUserByUserId(userId);
  }

  /**
   * Получает пользователя из репозитория по идентификатору.
   *
   * @param userId Идентификатор пользователя.
   * @return Заполненный объект пользователя или null, если не найден.
   */
  @Override
  public User getUserById(Long userId) {

    return this.userRepository.findById(userId).orElse(null);
  }

  /**
   * Получает пользователя из репозитория по электронному адресу.
   *
   * @param email Электронный адрес пользователя.
   * @return Заполненный объект пользователя или null, если не найден.
   */
  @Override
  public User getUserByEmail(String email) {
    return this.userRepository.find(email).orElse(null);

  }

  /**
   * Проверяет, существует ли пользователь с указанным электронным адресом.
   *
   * @param email Электронный адрес пользователя.
   * @return true, если пользователь существует.
   */
  @Override
  public boolean existUserByEmail(String email) {
    return this.userRepository.existUser(email);
  }
}

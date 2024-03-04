package com.denknd.services.impl;

import com.denknd.entity.Parameters;
import com.denknd.entity.Roles;
import com.denknd.entity.User;
import com.denknd.exception.AccessDeniedException;
import com.denknd.exception.InvalidUserDataException;
import com.denknd.exception.UserAlreadyExistsException;
import com.denknd.repository.UserRepository;
import com.denknd.security.service.SecurityService;
import com.denknd.services.UserService;
import com.denknd.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * Реализация сервиса для работы с пользователями.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final SecurityService securityService;

  /**
   * Регистрирует нового пользователя.
   *
   * @param user Полностью заполненный объект пользователя без идентификатора.
   * @return Полностью заполненный объект пользователя с идентификатором.
   * @throws UserAlreadyExistsException Если пользователь с таким электронным адресом уже существует.
   * @throws NoSuchAlgorithmException   Если алгоритм хэширования пароля не найден.
   * @throws InvalidUserDataException   Если переданные данные пользователя недопустимы.
   * @throws AccessDeniedException      Если пользователь уже аутентифицирован.
   */
  @Override
  public User registrationUser(User user) throws NoSuchAlgorithmException{
    if (this.securityService.isAuthentication()) {
      throw new AccessDeniedException("Вы уже зарегистрированы, если хотите создать еще один аккаунт, выйдите из этого аккаунта.");
    }
    if (this.userRepository.existUser(user.getEmail())) {
      throw new UserAlreadyExistsException("Данный пользователь уже существует");
    }
    user.setPassword(this.passwordEncoder.encode(user.getPassword()));
    user.setRole(Roles.USER);
    try {
      return this.userRepository.save(user);
    } catch (SQLException e) {
      throw new InvalidUserDataException("Данные переданные пользователям не валидные: " + e.getMessage());
    }
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
   * Получает пользователя из репозитория по идентификатору или адресу электронной почты.
   *
   * @param parameters Параметры запроса, содержащие идентификатор пользователя или адрес электронной почты(userId, email).
   * @return Заполненный объект пользователя или null, если не найден.
   */
  @Override
  public User getUser(Parameters parameters) {
    var userSecurity = this.securityService.getUserSecurity();
    if (userSecurity.role().equals(Roles.USER)) {
      return this.userRepository.findById(userSecurity.userId()).orElse(null);
    }
    if (userSecurity.role().equals(Roles.ADMIN)) {
      if (parameters.getUserId() != null) {
        return this.userRepository.findById(parameters.getUserId()).orElse(null);
      }
      if (parameters.getEmail() != null) {
        return this.userRepository.find(parameters.getEmail()).orElse(null);
      }
    }
    return null;
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

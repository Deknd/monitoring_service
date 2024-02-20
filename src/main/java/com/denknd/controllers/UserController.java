package com.denknd.controllers;

import com.denknd.aspectj.audit.AuditRecording;
import com.denknd.dto.UserCreateDto;
import com.denknd.dto.UserDto;
import com.denknd.exception.InvalidUserDataException;
import com.denknd.exception.UserAlreadyExistsException;
import com.denknd.mappers.UserMapper;
import com.denknd.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.NoSuchAlgorithmException;

/**
 * Контроллер для работы с пользователями
 */
@RequiredArgsConstructor
@Slf4j
public class UserController {
  /**
   * Сервис для управления пользователями
   */
  private final UserService userService;

  /**
   * Маппер пользователей
   */
  private final UserMapper userMapper;

  /**
   * Создает нового пользователя в системе
   *
   * @param userCreateDto пользователь полученный от пользователя
   * @return возвращает созданного пользователя
   * @throws UserAlreadyExistsException если данный пользователь уже создан
   * @throws NoSuchAlgorithmException если не получилось использовать алгоритм шифрования
   * @throws InvalidUserDataException если произошла ошибка при сохранении в БД
   */
  @AuditRecording("Регистрирует нового пользователя")
  public UserDto createUser(UserCreateDto userCreateDto) throws UserAlreadyExistsException, NoSuchAlgorithmException, InvalidUserDataException {
    var user = this.userMapper.mapUserCreateDtoToUser(userCreateDto);
    var result = this.userService.registrationUser(user);
    return this.userMapper.mapUserToUserDto(result);
  }

  /**
   * Получает данные пользователя, по идентификатору
   *
   * @param userId идентификатор пользователя
   * @return пользователь с данным идентификатор
   */
  @AuditRecording("Получает информацию о пользователе")
  public UserDto getUser(Long userId) {
    if (!this.userService.existUser(userId)) {
      return null;
    }
    var userById = this.userService.getUserById(userId);
    return this.userMapper.mapUserToUserDto(userById);
  }

  /**
   * Получает данные пользователя, по электронной почте
   *
   * @param email электронная почта
   * @return пользователь с данной электронной почтой
   */
  public UserDto getUser(String email) {
    if (!this.userService.existUserByEmail(email)) {
      return null;
    }
    var userByEmail = this.userService.getUserByEmail(email);
    return this.userMapper.mapUserToUserDto(userByEmail);
  }
}

package com.denknd.controllers;

import com.denknd.dto.UserCreateDto;
import com.denknd.dto.UserDto;
import com.denknd.entity.Roles;
import com.denknd.exception.UserAlreadyExistsException;
import com.denknd.mappers.UserMapper;
import com.denknd.services.RoleService;
import com.denknd.services.UserService;
import lombok.RequiredArgsConstructor;

import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Контроллер для работы с пользователями
 */
@RequiredArgsConstructor
public class UserController {
  /**
   * Сервис для управления пользователями
   */
  private final UserService userService;
  /**
   * Сервис для управления ролями.
   */
  private final RoleService roleService;

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
   */
  public UserDto createUser(UserCreateDto userCreateDto) throws UserAlreadyExistsException, NoSuchAlgorithmException {

    var user = this.userMapper.mapUserCreateDtoToUser(userCreateDto);
    var result = this.userService.registrationUser(user);
    this.roleService.addRoles(result.getUserId(), Roles.USER);
    return this.userMapper.mapUserToUserDto(result);
  }

  /**
   * Получает данные пользователя, по идентификатору
   *
   * @param userId идентификатор пользователя
   * @return пользователь с данным идентификатор
   */
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

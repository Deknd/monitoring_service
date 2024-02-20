package com.denknd.mappers;

import com.denknd.dto.UserCreateDto;
import com.denknd.dto.UserDto;
import com.denknd.entity.Roles;
import com.denknd.entity.User;
import com.denknd.security.entity.UserSecurity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Маппер для объекта пользователя.
 * Этот интерфейс предоставляет методы для преобразования объектов UserCreateDto в User и обратно,
 * объектов User в UserDto и объектов User в UserSecurity, а также из ResultSet в объекты User при работе с базой данных.
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  /**
   * Преобразование UserCreateDto в User.
   *
   * @param userCreateDto данные о пользователе от пользователя
   * @return объект пользователя для сервиса
   */
  User mapUserCreateDtoToUser(UserCreateDto userCreateDto);

  /**
   * Преобразование User в UserDto.
   *
   * @param user объект пользователя от сервиса
   * @return объект пользователя для пользователя
   */
  UserDto mapUserToUserDto(User user);

  /**
   * Преобразует User в UserSecurity.
   *
   * @param user объект пользователя от сервиса
   * @return объект пользователя для секьюрити
   */
  UserSecurity mapUserToUserSecurity(User user);

  /**
   * Преобразует данные из ResultSet в объект пользователя.
   *
   * @param resultSet данные из БД
   * @return объект пользователя
   * @throws SQLException ошибка при извлечении данных
   */
  default User mapResultSetToUser(ResultSet resultSet) throws SQLException {
    return User.builder()
            .userId(resultSet.getLong("user_id"))
            .email(resultSet.getString("email"))
            .password(resultSet.getString("password"))
            .firstName(resultSet.getString("user_name"))
            .lastName(resultSet.getString("user_last_name"))
            .role(Roles.valueOf(resultSet.getString("roles")))
            .build();
  }
}

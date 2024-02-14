package com.denknd.mappers;

import com.denknd.dto.UserCreateDto;
import com.denknd.dto.UserDto;
import com.denknd.entity.Roles;
import com.denknd.entity.User;
import com.denknd.security.entity.UserSecurity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Маппер для объекта юзера
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  /**
   * Преобразование UserCreateDto в User
   *
   * @param userCreateDto юзер от пользователя
   * @return юзер для сервиса
   */
  User mapUserCreateDtoToUser(UserCreateDto userCreateDto);

  /**
   * Преобразование User в UserDto
   *
   * @param user юзер от сервиса
   * @return юзер для пользователя
   */
  UserDto mapUserToUserDto(User user);

  /**
   * Преобразует User в UserSecurity
   *
   * @param user юзер от сервиса
   * @return юзер для секьюрити
   */
  UserSecurity mapUserToUserSecurity(User user);

  /**
   * преобразует данные из БД в объект пользователя
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

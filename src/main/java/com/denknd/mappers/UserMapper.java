package com.denknd.mappers;

import com.denknd.dto.UserCreateDto;
import com.denknd.dto.UserDto;
import com.denknd.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * Маппер для объекта юзера
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  /**
   * Преобразование UserCreateDto в User
   * @param userCreateDto юзер от пользователя
   * @return юзер для сервиса
   */
  User mapUserCreateDtoToUser(UserCreateDto userCreateDto);

  /**
   * Преобразование User в UserDto
   * @param user юзер от сервиса
   * @return юзер для пользователя
   */
  UserDto mapUserToUserDto(User user);
}

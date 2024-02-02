package com.denknd.mappers;

import com.denknd.dto.UserCreateDto;
import com.denknd.entity.Address;
import com.denknd.entity.Roles;
import com.denknd.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

  private UserMapper userMapper;

  @BeforeEach
  void setUp() {
    this.userMapper = UserMapper.INSTANCE;
  }

  @Test
  @DisplayName("Проверяет, что правильно маппит UserCreateDto в User")
  void mapUserCreateDtoToUser() {
    var userCreateDto = UserCreateDto.builder()
            .email("email")
            .password("password")
            .lastName("lastName")
            .firstName("firstName")
            .build();

    var user = this.userMapper.mapUserCreateDtoToUser(userCreateDto);

    assertThat(user).isNotNull();
    assertThat(user.getEmail()).isEqualTo(userCreateDto.email());
    assertThat(user.getPassword()).isEqualTo(userCreateDto.password());
    assertThat(user.getLastName()).isEqualTo(userCreateDto.lastName());
    assertThat(user.getFirstName()).isEqualTo(userCreateDto.firstName());
    assertThat(user.getUserId()).isNull();
    assertThat(user.getAddresses()).isNull();
    assertThat(user.getRole()).isNull();
  }
  @Test
  @DisplayName("Проверяет, что если отправить null, вернется null")
  void mapUserCreateDtoToUser_null() {
    var user = this.userMapper.mapUserCreateDtoToUser(null);

    assertThat(user).isNull();
  }

  @Test
  @DisplayName("Проверяет, что правильно маппит User в UserCreateDto")
  void mapUserToUserDto() {
    var user = User.builder()
            .userId(1L)
            .firstName("firstName")
            .lastName("lastName")
            .email("email")
            .password("password")
            .addresses(List.of(Address.builder().build()))
            .role(Roles.USER)
            .build();

    var userDto = this.userMapper.mapUserToUserDto(user);

    assertThat(userDto).isNotNull();
    assertThat(userDto.userId()).isEqualTo(user.getUserId());
    assertThat(userDto.email()).isEqualTo(user.getEmail());
    assertThat(userDto.lastName()).isEqualTo(user.getLastName());
    assertThat(userDto.firstName()).isEqualTo(user.getFirstName());

  }
  @Test
  @DisplayName("Проверяет, что правильно маппит User в UserCreateDto")
  void mapUserToUserDto_null() {
    var userDto = this.userMapper.mapUserToUserDto(null);

    assertThat(userDto).isNull();
  }
}
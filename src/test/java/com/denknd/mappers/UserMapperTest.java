package com.denknd.mappers;

import com.denknd.dto.UserCreateDto;
import com.denknd.entity.Address;
import com.denknd.entity.Roles;
import com.denknd.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
  @DisplayName("Проверяет, что если отправить null, то вернет null")
  void mapUserToUserDto_null() {
    var userDto = this.userMapper.mapUserToUserDto(null);
    assertThat(userDto).isNull();
  }

  @Test
  @DisplayName("Проверяет, что правильно маппит User в UserSecurity")
  void mapUserToUserSecurity() {
    var user = User.builder()
            .userId(1L)
            .firstName("firstName")
            .lastName("lastName")
            .email("email")
            .password("password")
            .addresses(List.of(Address.builder().build()))
            .role(Roles.USER)
            .build();

    var userSecurity = this.userMapper.mapUserToUserSecurity(user);

    assertThat(userSecurity).isNotNull();
    assertThat(userSecurity.userId()).isEqualTo(user.getUserId());
    assertThat(userSecurity.firstName()).isEqualTo(user.getFirstName());
    assertThat(userSecurity.role()).isEqualTo(user.getRole());
    assertThat(userSecurity.password()).isEqualTo(user.getPassword());
  }

  @Test
  @DisplayName("Проверяет, что если отправить null, то вернет null")
  void mapUserToUserSecurity_null() {
    var userSecurity = this.userMapper.mapUserToUserSecurity(null);

    assertThat(userSecurity).isNull();
  }

  @Test
  @DisplayName("Проверяет, что правильно маппится ResultSet в User")
  void mapResultSetToUser() throws SQLException {
    var resultSet = mock(ResultSet.class);
    when(resultSet.getLong("user_id")).thenReturn(1L);
    when(resultSet.getString("email")).thenReturn("email");
    when(resultSet.getString("password")).thenReturn("password");
    when(resultSet.getString("user_name")).thenReturn("user_name");
    when(resultSet.getString("user_last_name")).thenReturn("user_last_name");
    when(resultSet.getString("roles")).thenReturn("USER");

    var user = this.userMapper.mapResultSetToUser(resultSet);

    assertThat(user.getUserId()).isNotNull();
    assertThat(user.getEmail()).isNotNull();
    assertThat(user.getPassword()).isNotNull();
    assertThat(user.getFirstName()).isNotNull();
    assertThat(user.getLastName()).isNotNull();
    assertThat(user.getRole()).isNotNull();
  }

  @Test
  @DisplayName("Проверяет, что выпадает ошибка, если нет данного столбца")
  void mapResultSetToUser_SQLException() throws SQLException {
    var resultSet = mock(ResultSet.class);
    when(resultSet.getLong("user_id")).thenReturn(1L);
    when(resultSet.getString("email")).thenReturn("email");
    when(resultSet.getString("password")).thenThrow(SQLException.class);
    when(resultSet.getString("user_name")).thenReturn("user_name");
    when(resultSet.getString("user_last_name")).thenReturn("user_last_name");
    when(resultSet.getString("roles")).thenReturn("USER");

    assertThatThrownBy(() -> this.userMapper.mapResultSetToUser(resultSet));

  }
}
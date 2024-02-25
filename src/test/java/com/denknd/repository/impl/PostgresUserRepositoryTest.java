package com.denknd.repository.impl;

import com.denknd.config.ContainerConfig;
import com.denknd.entity.Roles;
import com.denknd.entity.User;
import com.denknd.mappers.UserMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.security.SecureRandom;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {ContainerConfig.class})
@ActiveProfiles("test")
class PostgresUserRepositoryTest {
  @Autowired
  private PostgresUserRepository repository;
  @SpyBean
  private UserMapper userMapper;
  private static SecureRandom random = new SecureRandom();
  private static String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  @Test
  @DisplayName("Проверяет, что пользователь существует")
  void existUser() {
    var email = "test@email.com";

    var existUser = this.repository.existUser(email);

    assertThat(existUser).isTrue();
  }
  @Test
  @DisplayName("Проверяет, что пользователь не существует")
  void existUser_noUser() {
    var email = "false@email.com";

    var existUser = this.repository.existUser(email);

    assertThat(existUser).isFalse();
  }
  @Test
  @DisplayName("Сохраняет нового пользователя в репозитории")
  void save() throws SQLException {
    var userTest = User.builder()
            .email(generateRandomLogin(10)+"@Email.com")
            .role(Roles.USER)
            .password("1234")
            .firstName(generateRandomLogin(12))
            .lastName(generateRandomLogin(10))
            .build();

    var saveUser = this.repository.save(userTest);

    assertThat(saveUser.getUserId()).isNotNull();
    assertThat(this.repository.existUser(saveUser.getEmail())).isTrue();
  }
  @Test
  @DisplayName("Проверяет, что не сохраняет нового пользователя, если установлен айди")
  void save_userId() throws SQLException {
    var userTest = User.builder()
            .userId(123L)
            .email(generateRandomLogin(10)+"@Email.com")
            .role(Roles.USER)
            .password("1234")
            .firstName(generateRandomLogin(12))
            .lastName(generateRandomLogin(10))
            .build();

    assertThatThrownBy(()->this.repository.save(userTest)).isInstanceOf(SQLException.class);

    assertThat(this.repository.existUser(userTest.getEmail())).isFalse();
  }


  @Test
  @DisplayName("Проверяет, что пользователь достается из памяти")
  void find() throws SQLException {
    var email = "test@email.com";

    var optionalUser = this.repository.find(email);

    assertThat(optionalUser).isPresent();
    verify(this.userMapper, times(1)).mapResultSetToUser(any());

  }
  @Test
  @DisplayName("Проверяет, что пользователь достается из памяти")
  void find_notUser(){
    var email = "test";

    var user = this.repository.find(email);

    assertThat(user).isEmpty();
  }
  @Test
  @DisplayName("проверяет что пользователь с данным айди существует")
  void existUserByUserId() throws SQLException {

    var result = this.repository.existUserByUserId(3L);

    assertThat(result).isTrue();
  }
  @Test
  @DisplayName("проверяет что пользователь с данным айди не существует")
  void existUserByUserId_notUser(){
    var userId = 12321543L;

    var result = this.repository.existUserByUserId(userId);

    assertThat(result).isFalse();
  }
  @Test
  @DisplayName("проверяет, что по айди достается пользователь")
  void findById() throws SQLException {

    var result = this.repository.findById(3L);

    assertThat(result).isPresent();
  }

  @Test
  @DisplayName("проверяет, что по не верному айди достается пустой опшинал")
  void findById_noUser(){
    var userId = 123L;

    var result = this.repository.findById(userId);

    assertThat(result).isEmpty();
  }
  public String generateRandomLogin(int length) {
    StringBuilder login = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
      login.append(ALLOWED_CHARACTERS.charAt(randomIndex));
    }
    return login.toString();
  }

}
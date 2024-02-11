package com.denknd.repository.impl;

import com.denknd.entity.Roles;
import com.denknd.entity.User;
import com.denknd.repository.TestContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostgresUserRepositoryTest extends TestContainer {
  private PostgresUserRepository repository;

  @BeforeEach
  void setUp() {
    this.repository = new PostgresUserRepository(postgresContainer.getDataBaseConnection());
  }

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
  @DisplayName("Проверяет, что не сохраняет нового пользователя, если слишком длинное имя")
  void save_longFirstName(){
    var userTest = User.builder()
            .email(generateRandomLogin(10)+"@Email.com")
            .role(Roles.USER)
            .password("1234")
            .firstName(generateRandomLogin(51))
            .lastName(generateRandomLogin(10))
            .build();

    assertThatThrownBy(()->this.repository.save(userTest)).isInstanceOf(SQLException.class);

    assertThat(this.repository.existUser(userTest.getEmail())).isFalse();
  }
  @Test
  @DisplayName("Проверяет, что не сохраняет нового пользователя, если слишком длинная фамилия")
  void save_longLastName(){
    var userTest = User.builder()
            .email(generateRandomLogin(10)+"@Email.com")
            .role(Roles.USER)
            .password("1234")
            .firstName(generateRandomLogin(10))
            .lastName(generateRandomLogin(51))
            .build();

    assertThatThrownBy(()->this.repository.save(userTest)).isInstanceOf(SQLException.class);

    assertThat(this.repository.existUser(userTest.getEmail())).isFalse();
  }

  @Test
  @DisplayName("Проверяет, что пользователь достается из памяти")
  void find(){
    var email = "test@email.com";

    var optionalUser = this.repository.find(email);

    assertThat(optionalUser).isPresent();
    var user = optionalUser.get();
    assertThat(user.getUserId()).isNotNull();
    assertThat(user.getEmail()).isEqualTo("test@email.com");
    assertThat(user.getRole()).isEqualTo(Roles.USER);
    assertThat(user.getPassword()).isEqualTo("a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3");
    assertThat(user.getFirstName()).isEqualTo("Danil");
    assertThat(user.getLastName()).isEqualTo("Random");
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
  void existUserByUserId(){
    var userId = this.repository.find("test@email.com").get().getUserId();

    var result = this.repository.existUserByUserId(userId);

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
  void findById(){
    var userId = this.repository.find("test@email.com").get().getUserId();

    var result = this.repository.findById(userId);

    assertThat(result).isPresent();
  }

  @Test
  @DisplayName("проверяет, что по не верному айди достается пустой опшинал")
  void findById_noUser(){
    var userId = 123L;

    var result = this.repository.findById(userId);

    assertThat(result).isEmpty();
  }


}
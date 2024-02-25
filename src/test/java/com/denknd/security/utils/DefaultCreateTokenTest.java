package com.denknd.security.utils;

import com.denknd.entity.Roles;
import com.denknd.security.entity.UserSecurity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultCreateTokenTest {
  private DefaultCreateToken defaultCreateToken;

  @BeforeEach
  void setUp() {
    this.defaultCreateToken = new DefaultCreateToken();
  }

  @Test
  @DisplayName("Проверяется, что создается полностью готовый токен")
  void apply() {
    var userSecurity = UserSecurity.builder()
            .userId(1L)
            .firstName("first")
            .role(Roles.USER)
            .build();

    var apply = this.defaultCreateToken.apply(userSecurity);

    assertThat(apply).hasNoNullFieldsOrProperties();
  }
}
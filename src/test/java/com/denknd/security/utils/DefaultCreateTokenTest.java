//package com.denknd.security.utils;
//
//import com.denknd.entity.Roles;
//import com.denknd.security.entity.Token;
//import com.denknd.security.entity.UserSecurity;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.time.Duration;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//
//class DefaultCreateTokenTest {
//  private DefaultCreateToken defaultCreateToken;
//  @BeforeEach
//  void setUp() {
//    this.defaultCreateToken = new DefaultCreateToken(Duration.ofHours(3));
//  }
//
//  @Test
//  @DisplayName("Проверяется, что создается полностью готовый токен")
//  void apply() {
//    var userSecurity = UserSecurity.builder()
//            .userId(1L)
//            .firstName("first")
//            .role(Roles.USER)
//            .build();
//
//    var apply = this.defaultCreateToken.apply(userSecurity);
//
//    assertThat(apply).hasNoNullFieldsOrProperties();
//  }
//}
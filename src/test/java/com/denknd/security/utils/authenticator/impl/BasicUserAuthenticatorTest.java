package com.denknd.security.utils.authenticator.impl;

import com.denknd.entity.User;
import com.denknd.mappers.UserMapper;
import com.denknd.security.entity.PreAuthenticatedAuthenticationToken;
import com.denknd.security.entity.UserCredentials;
import com.denknd.security.entity.UserSecurity;
import com.denknd.services.UserService;
import com.denknd.util.PasswordEncoder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BasicUserAuthenticatorTest {

  private AutoCloseable autoCloseable;
  @Mock
  private UserService userService;
  @Mock
  private UserMapper userMapper;
  @Mock
  private PasswordEncoder passwordEncoder;
  private BasicUserAuthenticator basicUserAuthenticator;
  @BeforeEach
  void setUp() {
    this.autoCloseable = MockitoAnnotations.openMocks(this);
    this.basicUserAuthenticator = new BasicUserAuthenticator(this.userService, this.userMapper, this.passwordEncoder);
  }

  @AfterEach
  void tearDown() throws Exception {
    this.autoCloseable.close();
  }

  @Test
  @DisplayName("Проверяет, что при вызове метода проходит по всем нужным методам и возвращает пользователя безопасности")
  void authentication() {
    var principal = new UserCredentials("email", "rawPassword");
    var preAuthenticatedAuthenticationToken = new PreAuthenticatedAuthenticationToken(
            principal, null);
    when(this.userService.existUserByEmail(eq(principal.email()))).thenReturn(true);
    when(this.userService.getUserByEmail(eq(principal.email()))).thenReturn(mock(User.class));
    when(this.userMapper.mapUserToUserSecurity(any())).thenReturn(mock(UserSecurity.class));
    when(this.passwordEncoder.matches(eq(principal.rawPassword()), any())).thenReturn(true);

    var authentication = this.basicUserAuthenticator.authentication(preAuthenticatedAuthenticationToken);

    assertThat(authentication).isNotNull();
  }
  @Test
  @DisplayName("Проверяет, что при вызове метода проходит по всем нужным методам и при не верном пароле возвращает null")
  void authentication_falsePassword() {
    var principal = new UserCredentials("email", "rawPassword");
    var preAuthenticatedAuthenticationToken = new PreAuthenticatedAuthenticationToken(
            principal, null);
    when(this.userService.existUserByEmail(eq(principal.email()))).thenReturn(true);
    when(this.userService.getUserByEmail(eq(principal.email()))).thenReturn(mock(User.class));
    when(this.userMapper.mapUserToUserSecurity(any())).thenReturn(mock(UserSecurity.class));
    when(this.passwordEncoder.matches(eq(principal.rawPassword()), any())).thenReturn(false);

    var authentication = this.basicUserAuthenticator.authentication(preAuthenticatedAuthenticationToken);

    assertThat(authentication).isNull();
  }
  @Test
  @DisplayName("Проверяет, что при вызове метода проходит по всем нужным методам и при отсутствие пользователя с данным email возвращает null")
  void authentication_notUser() {
    var principal = new UserCredentials("email", "rawPassword");
    var preAuthenticatedAuthenticationToken = new PreAuthenticatedAuthenticationToken(
            principal, null);
    when(this.userService.existUserByEmail(eq(principal.email()))).thenReturn(true);
    when(this.userService.getUserByEmail(eq(principal.email()))).thenReturn(null);

    var authentication = this.basicUserAuthenticator.authentication(preAuthenticatedAuthenticationToken);

    assertThat(authentication).isNull();
  }
  @Test
  @DisplayName("Проверяет, что при вызове метода с типом принципала не UserCredentials, возвращает null")
  void authentication_notAuthenticationToken() {
    var principal = "email";
    var preAuthenticatedAuthenticationToken = new PreAuthenticatedAuthenticationToken(
            principal, null);

    var authentication = this.basicUserAuthenticator.authentication(preAuthenticatedAuthenticationToken);

    assertThat(authentication).isNull();
  }
}
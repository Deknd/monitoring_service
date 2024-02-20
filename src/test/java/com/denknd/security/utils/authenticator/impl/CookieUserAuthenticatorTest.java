package com.denknd.security.utils.authenticator.impl;

import com.denknd.entity.Roles;
import com.denknd.security.entity.PreAuthenticatedAuthenticationToken;
import com.denknd.security.entity.Token;
import com.denknd.security.entity.UserSecurity;
import com.denknd.security.service.TokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class CookieUserAuthenticatorTest {

  private AutoCloseable autoCloseable;
  @Mock
  private TokenService tokenService;
  private CookieUserAuthenticator cookieUserAuthenticator;

  @BeforeEach
  void setUp() {
    this.autoCloseable = MockitoAnnotations.openMocks(this);
    this.cookieUserAuthenticator = new CookieUserAuthenticator(this.tokenService);
  }

  @AfterEach
  void tearDown() throws Exception {
    this.autoCloseable.close();
  }

  @Test
  @DisplayName("Проверяется, что создается объект UserSecurity из PreAuthenticatedAuthenticationToken на основе Token")
  void authentication() {
    var now = Instant.now();
    var token = Token.builder()
            .id(UUID.randomUUID())
            .userId(1L)
            .firstName("firstName")
            .role(Roles.USER.name())
            .expiresAt(now.plus(Duration.ofHours(2)))
            .createdAt(now)
            .build();
    when(this.tokenService.existsByTokenId(eq(token.id().toString()))).thenReturn(false);

    var authentication = this.cookieUserAuthenticator.authentication(
            new PreAuthenticatedAuthenticationToken(token, null));
    assertThat(authentication.firstName()).isEqualTo(token.firstName());
    assertThat(authentication.userId()).isEqualTo(token.userId());
    assertThat(authentication.role()).isEqualTo(Roles.valueOf(token.role()));
    assertSame(authentication.token(), token);
  }
  @Test
  @DisplayName("Проверяется, что если токен заблокирован, то вернется null")
  void authentication_isBlockToken() {
    var now = Instant.now();
    var token = Token.builder()
            .id(UUID.randomUUID())
            .userId(1L)
            .firstName("firstName")
            .role(Roles.USER.name())
            .expiresAt(now.plus(Duration.ofHours(2)))
            .createdAt(now)
            .build();
    when(this.tokenService.existsByTokenId(eq(token.id().toString()))).thenReturn(true);

    var authentication = this.cookieUserAuthenticator.authentication(
            new PreAuthenticatedAuthenticationToken(token, null));

    assertThat(authentication).isNull();
  }
  @Test
  @DisplayName("Проверяется, что если время жизни токена истекло, возвращает null")
  void authentication_isExpiresToken() {
    var now = Instant.now();
    var token = Token.builder()
            .id(UUID.randomUUID())
            .userId(1L)
            .firstName("firstName")
            .role(Roles.USER.name())
            .expiresAt(now.minus(Duration.ofHours(2)))
            .createdAt(now.minus(Duration.ofHours(3)))
            .build();

    var authentication = this.cookieUserAuthenticator.authentication(
            new PreAuthenticatedAuthenticationToken(token, null));

    assertThat(authentication).isNull();
  }
  @Test
  @DisplayName("Проверяется, что если принципал не Token, то вернется null")
  void authentication_notToken() {
    var now = Instant.now();

    var authentication = this.cookieUserAuthenticator.authentication(
            new PreAuthenticatedAuthenticationToken(now, null));

    assertThat(authentication).isNull();
  }
}
package com.denknd.security.utils.converter.impl;

import com.denknd.exception.BadCredentialsException;
import com.denknd.security.entity.UserCredentials;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BasicAuthenticationConverterTest {
  private BasicAuthenticationConverter basicAuthenticationConverter;

  @BeforeEach
  void setUp() {
    this.basicAuthenticationConverter = new BasicAuthenticationConverter();
  }

  @Test
  @DisplayName("Проверяет, что достает данные пользователя из запроса и создает объект PreAuthenticatedAuthenticationToken")
  void convert() throws BadCredentialsException {
    var request = mock(HttpServletRequest.class);
    var authorizationHeader = "Basic dXNlcjpwYXNzd29yZA==";
    when(request.getHeader("Authorization")).thenReturn(authorizationHeader);

    var token = this.basicAuthenticationConverter.convert(request);

    assertThat(token).isNotNull();
    assertThat(token.principal()).isInstanceOf(UserCredentials.class);
    var userCredentials = (UserCredentials) token.principal();
    assertThat(userCredentials.email()).isEqualTo("user");
    assertThat(userCredentials.rawPassword()).isEqualTo("password");
  }

  @Test
  @DisplayName("Не верный токен доступа")
  void convert_InvalidToken_ThrowsBadCredentialsException() {
    var request = mock(HttpServletRequest.class);
    var invalidAuthorizationHeader = "Basic invalidToken";
    when(request.getHeader("Authorization")).thenReturn(invalidAuthorizationHeader);

    assertThatThrownBy(() -> this.basicAuthenticationConverter.convert(request)).isInstanceOf(BadCredentialsException.class);
  }

  @Test
  @DisplayName("Отсутствует токен доступа")
  void convert_EmptyToken_ThrowsBadCredentialsException() {
    var request = mock(HttpServletRequest.class);
    var emptyTokenAuthorizationHeader = "Basic ";
    when(request.getHeader("Authorization")).thenReturn(emptyTokenAuthorizationHeader);

    assertThatThrownBy(() -> this.basicAuthenticationConverter.convert(request)).isInstanceOf(BadCredentialsException.class);
  }

  @Test
  @DisplayName("Отсутствие заголовка Basic")
  void convert_NullAuthorizationBasic_ReturnsNull() throws BadCredentialsException {
    var request = mock(HttpServletRequest.class);
    when(request.getHeader("Authorization")).thenReturn("sdf");

    var convert = this.basicAuthenticationConverter.convert(request);

    assertThat(convert).isNull();
  }

  @Test
  @DisplayName("Отсутствие заголовка Authorization")
  void convert_NullAuthorizationHeader_ReturnsNull() throws BadCredentialsException {
    var request = mock(HttpServletRequest.class);
    when(request.getHeader("Authorization")).thenReturn(null);

    var convert = this.basicAuthenticationConverter.convert(request);

    assertThat(convert).isNull();
  }

  @Test
  @DisplayName("Ошибка декодирования токена")
  void convert_InvalidBase64Token_ThrowsBadCredentialsException() {
    var request = mock(HttpServletRequest.class);
    var invalidAuthorizationHeader = "Basic invalid:Token";
    when(request.getHeader("Authorization")).thenReturn(invalidAuthorizationHeader);

    assertThatThrownBy(() -> this.basicAuthenticationConverter.convert(request)).isInstanceOf(BadCredentialsException.class);
  }
}
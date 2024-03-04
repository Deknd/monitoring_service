package com.denknd.security.utils.converter.impl;

import com.denknd.exception.BadCredentialsException;
import com.denknd.security.entity.Token;
import com.nimbusds.jose.JWEDecrypter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CookieAuthenticationConverterTest {
  private CookieAuthenticationConverter cookieAuthenticationConverter;
  private AutoCloseable autoCloseable;
  @Mock
  private Function<String, Token> deserializerToken;
  @Mock
  private JWEDecrypter jweDecrypter;

  @BeforeEach
  void setUp() {
    this.autoCloseable = MockitoAnnotations.openMocks(this);
    this.cookieAuthenticationConverter = new CookieAuthenticationConverter(this.jweDecrypter);
    this.cookieAuthenticationConverter.setDeserializerToken(deserializerToken);
  }

  @AfterEach
  void tearDown() throws Exception {
    this.autoCloseable.close();
  }

  @Test
  @DisplayName("Проверяет, что достается токен из Куки и десериализуется в объект токена, возвращает PreAuthenticatedAuthenticationToken")
  void convert() throws BadCredentialsException {
    var request = mock(HttpServletRequest.class);
    var cookies = new Cookie[1];
    var cookie = mock(Cookie.class);
    when(cookie.getName()).thenReturn("__Host-auth-token");
    cookies[0] = cookie;
    when(request.getCookies()).thenReturn(cookies);
    when(this.deserializerToken.apply(any())).thenReturn(mock(Token.class));

    var convert = this.cookieAuthenticationConverter.convert(request);

    assertThat(convert).isNotNull();
    assertThat(convert.principal()).isInstanceOf(Token.class);
  }

  @Test
  @DisplayName("Проверяет, что сли не находит куки с именем __Host-auth-token, возвращает null")
  void convert_notCookie() throws BadCredentialsException {
    var request = mock(HttpServletRequest.class);
    var cookies = new Cookie[1];
    var cookie = mock(Cookie.class);
    when(cookie.getName()).thenReturn("_token");
    cookies[0] = cookie;
    when(request.getCookies()).thenReturn(cookies);

    var convert = this.cookieAuthenticationConverter.convert(request);

    assertThat(convert).isNull();
  }

  @Test
  @DisplayName("Проверяет, что сли не находит куки с именем __Host-auth-token, возвращает null")
  void convert_cookieIsNull() throws BadCredentialsException {
    var request = mock(HttpServletRequest.class);
    var cookies = new Cookie[1];
    when(request.getCookies()).thenReturn(cookies);

    var convert = this.cookieAuthenticationConverter.convert(request);

    assertThat(convert).isNull();
  }

  @Test
  @DisplayName("Проверяет, что сли нет куки, вернется null")
  void convert_noСookies() throws BadCredentialsException {
    var request = mock(HttpServletRequest.class);

    var convert
            = this.cookieAuthenticationConverter.convert(request);

    assertThat(convert).isNull();
  }
}
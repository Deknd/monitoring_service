package com.denknd.in.filters;

import com.denknd.exception.BadCredentialsException;
import com.denknd.security.entity.PreAuthenticatedAuthenticationToken;
import com.denknd.security.entity.UserSecurity;
import com.denknd.security.service.SecurityService;
import com.denknd.security.utils.authenticator.UserAuthenticator;
import com.denknd.security.utils.converter.AuthenticationConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.PrintWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BasicAuthenticationFilterTest {
  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  @Mock
  private SecurityService securityService;

  @Mock
  private UserAuthenticator userAuthenticator;
  @Mock
  private ObjectMapper objectMapper;
  @Mock
  private AuthenticationConverter authenticationConverter;

  private BasicAuthenticationFilter basicAuthenticationFilter;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.basicAuthenticationFilter = new BasicAuthenticationFilter(this.objectMapper, this.securityService, this.userAuthenticator);
    this.basicAuthenticationFilter.setAuthenticationConverter(this.authenticationConverter);

  }

  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }

  @Test
  @DisplayName("Проверяет, что вызываются нужные методы, при обращении по запросу прописанном в данном фильтре")
  void doFilter() throws ServletException, IOException, BadCredentialsException {
    when(this.request.getMethod()).thenReturn("POST");
    when(this.request.getRequestURI()).thenReturn("/auth/login");
    when(this.authenticationConverter.convert(eq(this.request))).thenReturn(mock(PreAuthenticatedAuthenticationToken.class));
    when(this.userAuthenticator.authentication(any())).thenReturn(mock(UserSecurity.class));
    doNothing().when(this.securityService).addPrincipal(any(UserSecurity.class));

    this.basicAuthenticationFilter.doFilter(this.request, this.response, this.filterChain);

    verify(this.userAuthenticator, times(1)).authentication(any());
    verify(this.securityService, times(1)).addPrincipal(any(UserSecurity.class));
    verify(this.securityService, times(1)).onAuthentication(this.response);
    verify(this.response, times(1)).setStatus(eq(HttpServletResponse.SC_OK));
    verify(this.filterChain, times(0)).doFilter(this.request, this.response);

  }
  @Test
  @DisplayName("Проверяет, что при обращении с неверными данными пользователя, возвращается статус UNAUTHORIZED")
  void doFilter_failedLogin() throws ServletException, IOException, BadCredentialsException {
    when(this.request.getMethod()).thenReturn("POST");
    when(this.request.getRequestURI()).thenReturn("/auth/login");
    when(this.authenticationConverter.convert(eq(this.request))).thenReturn(mock(PreAuthenticatedAuthenticationToken.class));
    when(this.userAuthenticator.authentication(any())).thenReturn(null);
    when(this.response.getWriter()).thenReturn(mock(PrintWriter.class));

    this.basicAuthenticationFilter.doFilter(this.request, this.response, this.filterChain);

    verify(this.userAuthenticator, times(1)).authentication(any());
    verify(this.securityService, times(0)).addPrincipal(any());
    verify(this.securityService, times(0)).onAuthentication(any());
    verify(this.response, times(1)).setStatus(eq(HttpServletResponse.SC_UNAUTHORIZED));
    verify(this.filterChain, times(0)).doFilter(this.request, this.response);

  }
  @Test
  @DisplayName("Проверяет, что при обращении по данному урл без заголовка Authorization с обозначением Basic ")
  void doFilter_failedHeader() throws ServletException, IOException, BadCredentialsException {
    when(this.request.getMethod()).thenReturn("POST");
    when(this.request.getRequestURI()).thenReturn("/auth/login");
    when(this.authenticationConverter.convert(eq(this.request))).thenReturn(null);
    when(this.response.getWriter()).thenReturn(mock(PrintWriter.class));

    this.basicAuthenticationFilter.doFilter(this.request, this.response, this.filterChain);

    verify(this.userAuthenticator, times(0)).authentication(any());
    verify(this.securityService, times(0)).addPrincipal(any());
    verify(this.securityService, times(0)).onAuthentication(any());
    verify(this.response, times(1)).setStatus(eq(HttpServletResponse.SC_UNAUTHORIZED));
    verify(this.filterChain, times(0)).doFilter(this.request, this.response);

  }
  @Test
  @DisplayName("Проверяет, что при обращении по данному урл при расшифровки данных выкидывается ошибка ")
  void doFilter_BadCredentialsException() throws ServletException, IOException, BadCredentialsException {
    when(this.request.getMethod()).thenReturn("POST");
    when(this.request.getRequestURI()).thenReturn("/auth/login");
    when(this.authenticationConverter.convert(eq(this.request))).thenThrow(BadCredentialsException.class);
    when(this.response.getWriter()).thenReturn(mock(PrintWriter.class));

    this.basicAuthenticationFilter.doFilter(this.request, this.response, this.filterChain);

    verify(this.userAuthenticator, times(0)).authentication(any());
    verify(this.securityService, times(0)).addPrincipal(any());
    verify(this.securityService, times(0)).onAuthentication(any());
    verify(this.response, times(1)).setStatus(eq(HttpServletResponse.SC_UNAUTHORIZED));
    verify(this.filterChain, times(0)).doFilter(this.request, this.response);

  }
  @Test
  @DisplayName("Проверяет, что при обращении по данному урл при расшифровки данных выкидывается ошибка ")
  void doFilter_notUrl() throws ServletException, IOException, BadCredentialsException {
    when(this.request.getMethod()).thenReturn("POST");
    when(this.request.getRequestURI()).thenReturn("/res/login");


    this.basicAuthenticationFilter.doFilter(this.request, this.response, this.filterChain);

    verify(this.userAuthenticator, times(0)).authentication(any());
    verify(this.securityService, times(0)).addPrincipal(any());
    verify(this.securityService, times(0)).onAuthentication(any());
    verify(this.response, times(0)).setStatus(anyInt());
    verify(this.filterChain, times(1)).doFilter(this.request, this.response);

  }
  @Test
  @DisplayName("Проверяет, что урл ожидаемое")
  void getURL_PATTERNS() {
    var url = "/auth/login";

    var url_patterns = this.basicAuthenticationFilter.getURL_PATTERNS();

    assertThat(url_patterns).isEqualTo(url);
  }

}
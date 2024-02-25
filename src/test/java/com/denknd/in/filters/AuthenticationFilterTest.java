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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthenticationFilterTest {
  private AutoCloseable closeable;
  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;
  @Mock
  private SecurityService securityService;
  @Mock
  private AuthenticationConverter authenticationConverter;
  @Mock
  private UserAuthenticator userAuthenticator;
  @Mock
  private ObjectMapper objectMapper;
  private AuthenticationFilter authenticationFilter;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.authenticationFilter = new AuthenticationFilter(
            this.securityService,
            List.of(this.authenticationConverter),
            this.objectMapper,
            List.of(this.userAuthenticator));
    this.authenticationFilter.addIgnoredRequest("/test/url", "POST", "GET");
  }

  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }

  @Test
  @DisplayName("Проверяет, что при вызове урла и метода  из игнор списка, фильтр пропускает запрос дальше")
  void doFilter_ignore() throws ServletException, IOException, BadCredentialsException {
    when(this.request.getMethod()).thenReturn("POST");
    when(this.request.getRequestURI()).thenReturn("/test/url");

    this.authenticationFilter.doFilter(this.request, this.response, this.filterChain);

    verify(this.authenticationConverter, times(0)).convert(any());
    verify(this.userAuthenticator, times(0)).authentication(any());
    verify(this.securityService, times(0)).addPrincipal(any());
    verify(this.filterChain, times(1)).doFilter(this.request, this.response);
    verify(this.response, times(0)).setStatus(anyInt());

  }

  @Test
  @DisplayName("Проверяет, что при вызове урла и метода не из игнор списка, фильтр выполняет все нужные действия и пропускает дальше")
  void doFilter() throws ServletException, IOException, BadCredentialsException {
    when(this.request.getMethod()).thenReturn("POST");
    when(this.request.getRequestURI()).thenReturn("/url");
    when(this.authenticationConverter.convert(eq(this.request))).thenReturn(mock(PreAuthenticatedAuthenticationToken.class));
    when(this.userAuthenticator.authentication(any())).thenReturn(mock(UserSecurity.class));

    this.authenticationFilter.doFilter(this.request, this.response, this.filterChain);

    verify(this.authenticationConverter, times(1)).convert(any());
    verify(this.userAuthenticator, times(1)).authentication(any());
    verify(this.securityService, times(1)).addPrincipal(any());
    verify(this.filterChain, times(1)).doFilter(this.request, this.response);
    verify(this.response, times(0)).setStatus(anyInt());

  }

  @Test
  @DisplayName("Проверяет, что при вызове урла и метода не из игнор списка, при не действительном токене выкидывает со статусом UNAUTHORIZED")
  void doFilter_failedToken() throws ServletException, IOException, BadCredentialsException {
    when(this.request.getMethod()).thenReturn("POST");
    when(this.request.getRequestURI()).thenReturn("/url");
    when(this.response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.authenticationConverter.convert(eq(this.request))).thenReturn(mock(PreAuthenticatedAuthenticationToken.class));
    when(this.userAuthenticator.authentication(any())).thenReturn(null);

    this.authenticationFilter.doFilter(this.request, this.response, this.filterChain);

    verify(this.authenticationConverter, times(1)).convert(any());
    verify(this.userAuthenticator, times(1)).authentication(any());
    verify(this.securityService, times(0)).addPrincipal(any());
    verify(this.filterChain, times(0)).doFilter(this.request, this.response);
    verify(this.response, times(1)).setStatus(eq(HttpServletResponse.SC_UNAUTHORIZED));
  }

  @Test
  @DisplayName("Проверяет, что при вызове урла и метода не из игнор списка, при отсутствие токена выкидывает со статусом UNAUTHORIZED")
  void doFilter_notToken() throws ServletException, IOException, BadCredentialsException {
    when(this.request.getMethod()).thenReturn("POST");
    when(this.request.getRequestURI()).thenReturn("/url");
    when(this.response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.authenticationConverter.convert(eq(this.request))).thenReturn(null);

    this.authenticationFilter.doFilter(this.request, this.response, this.filterChain);

    verify(this.authenticationConverter, times(1)).convert(any());
    verify(this.userAuthenticator, times(0)).authentication(any());
    verify(this.securityService, times(0)).addPrincipal(any());
    verify(this.filterChain, times(0)).doFilter(this.request, this.response);
    verify(this.response, times(1)).setStatus(eq(HttpServletResponse.SC_UNAUTHORIZED));
  }

  @Test
  @DisplayName("Проверяет, что при вызове урла и метода не из игнор списка, при отсутствие токена выкидывает со статусом UNAUTHORIZED")
  void doFilter_BadCredentialsException() throws ServletException, IOException, BadCredentialsException {
    when(this.request.getMethod()).thenReturn("POST");
    when(this.request.getRequestURI()).thenReturn("/url");
    when(this.response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.authenticationConverter.convert(eq(this.request))).thenThrow(BadCredentialsException.class);

    this.authenticationFilter.doFilter(this.request, this.response, this.filterChain);

    verify(this.authenticationConverter, times(1)).convert(any());
    verify(this.userAuthenticator, times(0)).authentication(any());
    verify(this.securityService, times(0)).addPrincipal(any());
    verify(this.filterChain, times(0)).doFilter(this.request, this.response);
    verify(this.response, times(1)).setStatus(eq(HttpServletResponse.SC_UNAUTHORIZED));
  }

}
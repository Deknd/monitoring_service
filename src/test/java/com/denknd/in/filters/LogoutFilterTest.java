package com.denknd.in.filters;

import com.denknd.security.service.SecurityService;
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
import org.mockito.internal.configuration.MockAnnotationProcessor;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LogoutFilterTest {
  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;
  private AutoCloseable autoCloseable;
  @Mock
  private SecurityService securityService;
  @Mock
  private ObjectMapper objectMapper;
  private LogoutFilter logoutFilter;

  @BeforeEach
  void setUp() {
    this.autoCloseable = MockitoAnnotations.openMocks(this);
    this.logoutFilter = new LogoutFilter(this.securityService);
  }

  @AfterEach
  void tearDown() throws Exception {
    this.autoCloseable.close();
  }

  @Test
  @DisplayName("Проверяет, что если сделать вызов по урлу из фильтра, то вызовется нужный сервис")
  void doFilter() throws ServletException, IOException {
    when(this.request.getRequestURI()).thenReturn(this.logoutFilter.getURL_PATTERNS());
    when(this.request.getMethod()).thenReturn(this.logoutFilter.getHTTP_METHOD());

    this.logoutFilter.doFilter(this.request, this.response, this.filterChain);

    verify(this.securityService, times(1)).logout(eq(this.response));
    verify(this.filterChain, times(0)).doFilter(eq(this.request), eq(this.response));
  }
  @Test
  @DisplayName("Проверяет, что если сделать вызов по урлу из фильтра, то вызовется нужный сервис")
  void doFilter_notPatternUrl() throws ServletException, IOException {
    when(this.request.getRequestURI()).thenReturn("/test");
    when(this.request.getMethod()).thenReturn(this.logoutFilter.getHTTP_METHOD());

    this.logoutFilter.doFilter(this.request, this.response, this.filterChain);

    verify(this.securityService, times(0)).logout(eq(this.response));
    verify(this.filterChain, times(1)).doFilter(eq(this.request), eq(this.response));
  }

  @Test
  @DisplayName("Проверяет, что урл соответствует ожидаемому")
  void getURL_PATTERNS() {
    var url = "/auth/logout";

    var url_patterns = this.logoutFilter.getURL_PATTERNS();

    assertThat(url_patterns).isEqualTo(url);
  }

  @Test
  void getHTTP_METHOD() {
    var method = "POST";

    var http_method = this.logoutFilter.getHTTP_METHOD();

    assertThat(http_method).isEqualTo(method);
  }

  @Test
  void getSecurityService() {
    var securityService1 = this.logoutFilter.getSecurityService();

    assertThat(securityService1).isNotNull();
  }


}
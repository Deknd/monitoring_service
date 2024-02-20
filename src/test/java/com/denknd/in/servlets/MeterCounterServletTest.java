package com.denknd.in.servlets;

import com.denknd.config.ManualConfig;
import com.denknd.controllers.CounterInfoController;
import com.denknd.entity.Roles;
import com.denknd.security.entity.UserSecurity;
import com.denknd.security.service.SecurityService;
import com.denknd.util.impl.Validators;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MeterCounterServletTest {

  private AutoCloseable autoCloseable;
  @Mock
  private ObjectMapper objectMapper;
  @Mock
  private SecurityService securityService;
  @Mock
  private Validators validator;
  @Mock
  private CounterInfoController counterInfoController;
  @Mock
  private ManualConfig manualConfig;
  @Mock
  private ServletContext servletContext;
  @Mock
  private ServletConfig servletConfig;
  private MeterCounterServlet meterCounterServlet;

  @BeforeEach
  void setUp() throws ServletException {
    this.autoCloseable = MockitoAnnotations.openMocks(this);
    when(this.manualConfig.getObjectMapper()).thenReturn(this.objectMapper);
    when(this.manualConfig.getSecurityService()).thenReturn(this.securityService);
    when(this.manualConfig.getValidator()).thenReturn(this.validator);
    when(this.manualConfig.getCounterInfoController()).thenReturn(this.counterInfoController);
    when(this.servletContext.getAttribute("context")).thenReturn(this.manualConfig);
    when(this.servletConfig.getServletContext()).thenReturn(this.servletContext);
    this.meterCounterServlet = new MeterCounterServlet();
    this.meterCounterServlet.init(servletConfig);
  }

  @AfterEach
  void tearDown() throws Exception {
    autoCloseable.close();
  }

  @Test
  @DisplayName("Проверяет, что при вызове данного эндпоинта админом, вызываются все методы и возвращается статус ОК")
  void doPut() throws IOException, SQLException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.meterCounterServlet.getMeterCounterPatch());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().role(Roles.ADMIN).build());

    this.meterCounterServlet.doPut(request, response);

    verify(this.counterInfoController, times(1)).addInfoForMeter(any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    verify(objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что при вызове данного эндпоинта админом выпадании ошибки, возвращается статус BAD_REQUEST")
  void doPut_SQLException() throws IOException, SQLException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.meterCounterServlet.getMeterCounterPatch());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().role(Roles.ADMIN).build());
    when(this.counterInfoController.addInfoForMeter(any())).thenThrow(SQLException.class);

    this.meterCounterServlet.doPut(request, response);

    verify(this.counterInfoController, times(1)).addInfoForMeter(any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что при вызове данного эндпоинта юзером возвращается ответ со статусом NOT_FOUND")
  void doPut_userRole() throws IOException, SQLException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.meterCounterServlet.getMeterCounterPatch());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().role(Roles.USER).build());


    this.meterCounterServlet.doPut(request, response);

    verify(this.counterInfoController, times(0)).addInfoForMeter(any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
    verify(objectMapper, times(0)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что при вызове не известного эндпоинта юзером возвращается ответ со статусом NOT_FOUND")
  void doPut_failedUrl() throws IOException, SQLException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.meterCounterServlet.getMeterCounterPatch()+"dsfs");
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);

    this.meterCounterServlet.doPut(request, response);

    verify(this.counterInfoController, times(0)).addInfoForMeter(any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
    verify(this.objectMapper, times(0)).writeValue(any(PrintWriter.class), any());
  }

  @Test
  @DisplayName("проверяет, что метод возвращает переданный objectMapper")
  void getObjectMapper(){
    assertThat( this.meterCounterServlet.getObjectMapper()).isEqualTo(this.objectMapper);
  }
}
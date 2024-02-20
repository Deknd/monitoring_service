package com.denknd.in.servlets;

import com.denknd.config.ManualConfig;
import com.denknd.controllers.TypeMeterController;
import com.denknd.entity.Roles;
import com.denknd.exception.ConstraintViolationException;
import com.denknd.exception.TypeMeterAdditionException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MeterTypesServletTest {

  private AutoCloseable autoCloseable;
  @Mock
  private ObjectMapper objectMapper;
  @Mock
  private SecurityService securityService;
  @Mock
  private Validators validator;
  @Mock
  private TypeMeterController typeMeterController;
  @Mock
  private ManualConfig manualConfig;
  @Mock
  private ServletContext servletContext;
  @Mock
  private ServletConfig servletConfig;
  private MeterTypesServlet meterTypesServlet;

  @BeforeEach
  void setUp() throws ServletException {
    this.autoCloseable = MockitoAnnotations.openMocks(this);
    when(this.manualConfig.getObjectMapper()).thenReturn(this.objectMapper);
    when(this.manualConfig.getSecurityService()).thenReturn(this.securityService);
    when(this.manualConfig.getValidator()).thenReturn(this.validator);
    when(this.manualConfig.getTypeMeterController()).thenReturn(this.typeMeterController);
    when(this.servletContext.getAttribute("context")).thenReturn(this.manualConfig);
    when(this.servletConfig.getServletContext()).thenReturn(this.servletContext);
    this.meterTypesServlet = new MeterTypesServlet();
    this.meterTypesServlet.init(this.servletConfig);
  }

  @AfterEach
  void tearDown() throws Exception {
    autoCloseable.close();
  }

  @Test
  @DisplayName("Проверяет, что при вызове данного эндпоинта, вызывается контроллер и создается ответ")
  void doPost() throws IOException, ServletException, TypeMeterAdditionException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.meterTypesServlet.getMeterTypesPatch());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().role(Roles.ADMIN).build());

    this.meterTypesServlet.doPost(request, response);

    verify(this.typeMeterController, times(1)).addNewType(any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_CREATED);
    verify(objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }

  @Test
  @DisplayName("Проверяет, что при вызове данного эндпоинта, при выкидывание ошибки возвращается статус BAD_REQUEST")
  void doPost_validationError() throws IOException, ServletException, TypeMeterAdditionException, ConstraintViolationException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.meterTypesServlet.getMeterTypesPatch());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().role(Roles.ADMIN).build());
    doThrow(ConstraintViolationException.class).when(this.validator).validate(any());


    this.meterTypesServlet.doPost(request, response);

    verify(this.typeMeterController, times(0)).addNewType(any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }

  @Test
  @DisplayName("Проверяет, что при вызове данного эндпоинта  ролью ЮЗЕР, возвращается статус FORBIDDEN")
  void doPost_userRole() throws IOException, ServletException, TypeMeterAdditionException, ConstraintViolationException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.meterTypesServlet.getMeterTypesPatch());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().role(Roles.USER).build());
    doThrow(ConstraintViolationException.class).when(this.validator).validate(any());


    this.meterTypesServlet.doPost(request, response);

    verify(this.typeMeterController, times(0)).addNewType(any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что при вызове данного эндпоинта  с не верным урл, возвращается статус NOT_FOUND")
  void doPost_failedRole() throws IOException, ServletException, TypeMeterAdditionException, ConstraintViolationException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.meterTypesServlet.getMeterTypesPatch()+"ваыв");
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().role(Roles.USER).build());
    doThrow(ConstraintViolationException.class).when(this.validator).validate(any());


    this.meterTypesServlet.doPost(request, response);

    verify(this.typeMeterController, times(0)).addNewType(any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
    verify(objectMapper, times(0)).writeValue(any(PrintWriter.class), any());
  }

  @Test
  @DisplayName("Проверяет, что при вызове данного эндпоинта, происходит вызов контроллера и формирования ответа")
  void doGet() throws IOException, ServletException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.meterTypesServlet.getMeterTypesPatch());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);

    this.meterTypesServlet.doGet(request, response);

    verify(this.typeMeterController, times(1)).getTypeMeterCodes();
    verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    verify(objectMapper, times(1)).writeValue(any(PrintWriter.class), any());

  }
  @Test
  @DisplayName("Проверяет, что при вызове данного эндпоинта не авторизированным, выкидывает со статусом FORBIDDEN")
  void doGet_forbidden() throws IOException, ServletException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.meterTypesServlet.getMeterTypesPatch());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(false);

    this.meterTypesServlet.doGet(request, response);

    verify(this.typeMeterController, times(0)).getTypeMeterCodes();
    verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что при вызове данного эндпоинта с не известной урл, выкидывает статус NOT_FOUND")
  void doGet_failedUrl() throws IOException, ServletException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.meterTypesServlet.getMeterTypesPatch()+"asd");
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));

    this.meterTypesServlet.doGet(request, response);

    verify(this.typeMeterController, times(0)).getTypeMeterCodes();
    verify(response, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
    verify(objectMapper, times(0)).writeValue(any(PrintWriter.class), any());
  }

  @Test
  @DisplayName("Проверяет, что метод возвращает объект, который был передан в конструктор")
  void getObjectMapper() {
    assertThat(this.meterTypesServlet.getObjectMapper()).isEqualTo(this.objectMapper);
  }
}
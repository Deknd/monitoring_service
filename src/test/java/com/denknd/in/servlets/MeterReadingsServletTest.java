package com.denknd.in.servlets;

import com.denknd.config.ManualConfig;
import com.denknd.controllers.MeterReadingController;
import com.denknd.entity.Roles;
import com.denknd.exception.MeterReadingConflictError;
import com.denknd.security.entity.UserSecurity;
import com.denknd.security.service.SecurityService;
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
import java.time.YearMonth;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MeterReadingsServletTest {
  private AutoCloseable autoCloseable;
  @Mock
  private ObjectMapper objectMapper;
  @Mock
  private MeterReadingController meterReadingController;
  @Mock
  private SecurityService securityService;
  @Mock
  private Function<String, Set<Long>> typeMeterParametersParserFromRawParameters;
  @Mock
  private Function<String, Long> longIdParserFromRawParameters;
  @Mock
  private Function<String, YearMonth> dateParserFromRawParameter;
  @Mock
  private ManualConfig manualConfig;
  @Mock
  private ServletContext servletContext;
  @Mock
  private ServletConfig servletConfig;
  private MeterReadingsServlet meterReadingsServlet;
  @BeforeEach
  void setUp() throws ServletException {
    this.autoCloseable = MockitoAnnotations.openMocks(this);
    when(this.manualConfig.getObjectMapper()).thenReturn(this.objectMapper);
    when(this.manualConfig.getMeterReadingController()).thenReturn(this.meterReadingController);
    when(this.manualConfig.getSecurityService()).thenReturn(this.securityService);
    when(this.servletContext.getAttribute("context")).thenReturn(this.manualConfig);
    when(this.servletConfig.getServletContext()).thenReturn(this.servletContext);
    this.meterReadingsServlet = new MeterReadingsServlet();
    this.meterReadingsServlet.init(this.servletConfig);
    this.meterReadingsServlet.setDateParserFromRawParameter(this.dateParserFromRawParameter);
    this.meterReadingsServlet.setLongIdParserFromRawParameters(this.longIdParserFromRawParameters);
    this.meterReadingsServlet.setTypeMeterParametersParserFromRawParameters(this.typeMeterParametersParserFromRawParameters);
  }

  @AfterEach
  void tearDown() throws Exception {
    this.autoCloseable.close();
  }

  @Test
  @DisplayName("Проверяет, что при попытке отправить показания с ролью юзер, выполняет все нужные действия и записывает все в ответ")
  void doPost() throws IOException, ServletException, MeterReadingConflictError {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.meterReadingsServlet.getSendMeterReading());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().userId(12L).role(Roles.USER).build());

    this.meterReadingsServlet.doPost(request, response);

    verify(this.meterReadingController, times(1)).addMeterReadingValue(any(), anyLong());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_CREATED);
    verify(this.objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что при попытке отправить показания с ролью юзер, выполняет все нужные действия, но при сохранении выпадает ошибка")
  void doPost_MeterReadingConflictError() throws IOException, ServletException, MeterReadingConflictError {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.meterReadingsServlet.getSendMeterReading());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().userId(12L).role(Roles.USER).build());
    when(this.meterReadingController.addMeterReadingValue(any(), any())).thenThrow(MeterReadingConflictError.class);

    this.meterReadingsServlet.doPost(request, response);

    verify(this.meterReadingController, times(1)).addMeterReadingValue(any(), anyLong());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_CONFLICT);
    verify(this.objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что при попытке отправить показания, не авторизовавшись выпадает статус форбиден")
  void doPost_forbidden() throws IOException, ServletException, MeterReadingConflictError {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.meterReadingsServlet.getSendMeterReading());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().userId(12L).role(Roles.ADMIN).build());

    this.meterReadingsServlet.doPost(request, response);

    verify(this.meterReadingController, times(0)).addMeterReadingValue(any(), anyLong());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(this.objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что при попытке отправить показания, введен не тот урл")
  void doPost_failedUrl() throws IOException, ServletException, MeterReadingConflictError {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.meterReadingsServlet.getSendMeterReading()+"sdf");
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);

    this.meterReadingsServlet.doPost(request, response);

    verify(this.meterReadingController, times(0)).addMeterReadingValue(any(), anyLong());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
    verify(this.objectMapper, times(0)).writeValue(any(PrintWriter.class), any());
  }

  @Test
  @DisplayName("Проверяет, что при обращении на данный эндпоинт с ролью юзер, вызываются нужный контроллер")
  void doGet_history_user() throws IOException, ServletException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.meterReadingsServlet.getHistoryMeterReading());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().userId(12L).role(Roles.USER).build());

    this.meterReadingsServlet.doGet(request, response);

    verify(this.meterReadingController, times(1)).getHistoryMeterReading(any(), any(), any(), any(), any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    verify(this.objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что при обращении на данный эндпоинт с ролью юзер, вызываются нужный контроллер и передан обязательный параметр")
  void doGet_history_admin() throws IOException, ServletException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.meterReadingsServlet.getHistoryMeterReading());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().userId(12L).role(Roles.ADMIN).build());
    when(this.longIdParserFromRawParameters.apply(any())).thenReturn(1L);

    this.meterReadingsServlet.doGet(request, response);

    verify(this.meterReadingController, times(1)).getHistoryMeterReading(any(), any(), any(), any(), any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    verify(this.objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что при обращении на данный эндпоинт с ролью юзер,не вызываются нужный контроллер и не передан обязательный параметр")
  void doGet_history_admin_notParamUserId() throws IOException, ServletException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.meterReadingsServlet.getHistoryMeterReading());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().userId(12L).role(Roles.ADMIN).build());

    this.meterReadingsServlet.doGet(request, response);

    verify(this.meterReadingController, times(0)).getHistoryMeterReading(any(), any(), any(), any(), any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(this.objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что при обращении на данный эндпоинт с ролью юзер, вызывается нужный контроллер и добавляется ответ")
  void doGet_meterReadings_user() throws IOException, ServletException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.meterReadingsServlet.getGetMeterReadings());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().userId(12L).role(Roles.USER).build());

    this.meterReadingsServlet.doGet(request, response);

    verify(this.meterReadingController, times(1)).getMeterReadings(any(), any(), any(), any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    verify(this.objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что при обращении на данный эндпоинт с ролью admin, вызывается нужный контроллер и добавляется ответ")
  void doGet_meterReadings_admin() throws IOException, ServletException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.meterReadingsServlet.getGetMeterReadings());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().userId(12L).role(Roles.ADMIN).build());
    when(this.longIdParserFromRawParameters.apply(any())).thenReturn(12L);

    this.meterReadingsServlet.doGet(request, response);

    verify(this.meterReadingController, times(1)).getMeterReadings(any(), any(), any(), any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    verify(this.objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что при обращении на данный эндпоинт с ролью admin, когда не передается обязательный для админа параметр")
  void doGet_meterReadings_admin_notParamUserId() throws IOException, ServletException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.meterReadingsServlet.getGetMeterReadings());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().userId(12L).role(Roles.ADMIN).build());

    this.meterReadingsServlet.doGet(request, response);

    verify(this.meterReadingController, times(0)).getMeterReadings(any(), any(), any(), any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(this.objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что при обращении на данный эндпоинт с не правильным урлом возвращается статус NOT_FOUND")
  void doGet_failedUrl() throws IOException, ServletException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.meterReadingsServlet.getGetMeterReadings()+"sddsf");
    var response = mock(HttpServletResponse.class);

    this.meterReadingsServlet.doGet(request, response);

    verify(this.meterReadingController, times(0)).getMeterReadings(any(), any(), any(), any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
    verify(this.objectMapper, times(0)).writeValue(any(PrintWriter.class), any());
  }


  @Test
  void getObjectMapper() {
  }
}
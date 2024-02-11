package com.denknd.in.servlets;

import com.denknd.config.ManualConfig;
import com.denknd.controllers.AddressController;
import com.denknd.entity.Roles;
import com.denknd.exception.AddressDatabaseException;
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
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AddressServletTest {
  private AutoCloseable autoCloseable;
  @Mock
  private ObjectMapper objectMapper;
  @Mock
  private Validators validators;
  @Mock
  private SecurityService securityService;
  @Mock
  private AddressController addressController;
  @Mock
  private Function<String, Long> longIdParserFromRawParameters;
  @Mock
  private ServletConfig config;
  @Mock
  private ManualConfig manualConfig;
  @Mock
  private ServletContext servletContext;

  private AddressServlet addressServlet;

  @BeforeEach
  void setUp() throws ServletException {
    this.autoCloseable = MockitoAnnotations.openMocks(this);
    when(this.manualConfig.getObjectMapper()).thenReturn(this.objectMapper);
    when(this.manualConfig.getValidator()).thenReturn(this.validators);
    when(this.manualConfig.getSecurityService()).thenReturn(this.securityService);
    when(this.manualConfig.getAddressController()).thenReturn(this.addressController);
    when(this.config.getServletContext()).thenReturn(this.servletContext);
    when(this.servletContext.getAttribute(eq("context"))).thenReturn(this.manualConfig);
    this.addressServlet = new AddressServlet();
    this.addressServlet.init(this.config);
    this.addressServlet.setLongIdParserFromRawParameters(this.longIdParserFromRawParameters);
  }

  @AfterEach
  void tearDown() throws Exception {
    autoCloseable.close();
  }

  @Test
  @DisplayName("Проверяет, что при совпадении урла, пост метод для пользователя с ролью юзер вызывает все нужные методы")
  void doPost() throws ServletException, IOException, AddressDatabaseException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.addressServlet.getAddAddress());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().role(Roles.USER).build());
    when(this.securityService.isAuthentication()).thenReturn(true);
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));

    this.addressServlet.doPost(request, response);

    verify(this.addressController, times(1)).addAddress(any(), any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_CREATED);
    verify(this.objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что при совпадении урла, пост метод для пользователя с ролью юзер вызывает все нужные методы, при выпадении ошибки")
  void doPost_AddressDatabaseException() throws ServletException, IOException, AddressDatabaseException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.addressServlet.getAddAddress());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().role(Roles.USER).build());
    when(this.securityService.isAuthentication()).thenReturn(true);
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.addressController.addAddress(any(), any())).thenThrow(AddressDatabaseException.class);

    this.addressServlet.doPost(request, response);

    verify(this.addressController, times(1)).addAddress(any(), any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(this.objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что при совпадении урла, пост метод для пользователя с ролью admin не вызываются ни какие контролеры")
  void doPost_admin() throws ServletException, IOException, AddressDatabaseException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.addressServlet.getAddAddress());
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().role(Roles.ADMIN).build());
    when(this.securityService.isAuthentication()).thenReturn(true);
    var response = mock(HttpServletResponse.class);

    this.addressServlet.doPost(request, response);

    verify(this.addressController, times(0)).addAddress(any(), any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
    verify(this.objectMapper, times(0)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что при не совпадении урла, пост метод не вызывает ни какие методы")
  void doPost_failedUrl() throws ServletException, IOException, AddressDatabaseException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.addressServlet.getAddAddress()+"dssad");
    var response = mock(HttpServletResponse.class);

    this.addressServlet.doPost(request, response);

    verify(this.addressController, times(0)).addAddress(any(), any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
    verify(this.objectMapper, times(0)).writeValue(any(PrintWriter.class), any());
  }

  @Test
  @DisplayName("Проверяет, что с нужным урл вызываются все сервисы и результат записывается в ответ")
  void doGet() throws ServletException, IOException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.addressServlet.getGetAddress());
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().role(Roles.USER).build());

    this.addressServlet.doGet(request, response);

    verify(this.addressController, times(1)).getAddress(any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    verify(this.objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }

  @Test
  @DisplayName("Проверяет, что с нужным урл и с ролью Админа вызываются все сервисы и результат записывается в ответ")
  void doGet_adminRole() throws ServletException, IOException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.addressServlet.getGetAddress());
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().role(Roles.ADMIN).build());
    when(this.longIdParserFromRawParameters.apply(any())).thenReturn(1L);

    this.addressServlet.doGet(request, response);

    verify(this.addressController, times(1)).getAddress(any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    verify(this.objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что с нужным урл и с ролью Админа вызываются все сервисы и результат записывается в ответ, при отсутствие переданного айди пользователя")
  void doGet_adminRole_notParam() throws ServletException, IOException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.addressServlet.getGetAddress());
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().role(Roles.ADMIN).build());
    when(this.longIdParserFromRawParameters.apply(any())).thenReturn(null);

    this.addressServlet.doGet(request, response);

    verify(this.addressController, times(0)).getAddress(any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(this.objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что не авторизованнпользователямм, не доступна данный запрос")
  void doGet_notAuthentication() throws ServletException, IOException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.addressServlet.getGetAddress());
    var response = mock(HttpServletResponse.class);
    when(this.securityService.isAuthentication()).thenReturn(false);

    this.addressServlet.doGet(request, response);

    verify(this.addressController, times(0)).getAddress(any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
    verify(this.objectMapper, times(0)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что не доступен запрос, если не верный урл")
  void doGet_failedUrl() throws ServletException, IOException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.addressServlet.getGetAddress()+"DSfsd");
    var response = mock(HttpServletResponse.class);

    this.addressServlet.doGet(request, response);

    verify(this.addressController, times(0)).getAddress(any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
    verify(this.objectMapper, times(0)).writeValue(any(PrintWriter.class), any());
  }

  @Test
  @DisplayName("Проверяется, что возвращается objectMapper из контекста")
  void getObjectMapper() {
    var objectMapper1 = this.addressServlet.getObjectMapper();

    assertThat(objectMapper1).isEqualTo(this.objectMapper);
  }
  @Test
  @DisplayName("Проверяется, что возможна настройка урлов")
  void setAddAddress(){
    var url = "testAddressUrl";

    this.addressServlet.setAddAddress(url);

    assertThat(this.addressServlet.getAddAddress()).isEqualTo(url);
  }
  @Test
  @DisplayName("Проверяется, что возможна настройка урлов")
  void setGetAddress(){
    var url = "testAddressUrl";

    this.addressServlet.setGetAddress(url);

    assertThat(this.addressServlet.getGetAddress()).isEqualTo(url);
  }

  @Test
  @DisplayName("Проверяется, что возвращается параметр")
  void getParamUserId(){
    assertThat(this.addressServlet.getParamUserId()).isEqualTo("userId");
  }

}
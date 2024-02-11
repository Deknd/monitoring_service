package com.denknd.in.servlets;

import com.denknd.config.ManualConfig;
import com.denknd.controllers.TypeMeterController;
import com.denknd.controllers.UserController;
import com.denknd.entity.Roles;
import com.denknd.exception.InvalidUserDataException;
import com.denknd.exception.UserAlreadyExistsException;
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
import java.security.NoSuchAlgorithmException;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServletTest {

  private AutoCloseable autoCloseable;
  @Mock
  private ObjectMapper objectMapper;
  @Mock
  private SecurityService securityService;
  @Mock
  private Validators validator;
  @Mock
  private UserController userController;
  @Mock
  private Function<String, Long> longIdParserFromRawParameters;

  @Mock
  private ManualConfig manualConfig;
  @Mock
  private ServletContext servletContext;
  @Mock
  private ServletConfig servletConfig;
  private UserServlet userServlet;

  @BeforeEach
  void setUp() throws ServletException {
    this.autoCloseable = MockitoAnnotations.openMocks(this);
    when(this.manualConfig.getObjectMapper()).thenReturn(this.objectMapper);
    when(this.manualConfig.getSecurityService()).thenReturn(this.securityService);
    when(this.manualConfig.getValidator()).thenReturn(this.validator);
    when(this.manualConfig.getUserController()).thenReturn(this.userController);
    when(this.servletContext.getAttribute("context")).thenReturn(this.manualConfig);
    when(this.servletConfig.getServletContext()).thenReturn(this.servletContext);
    this.userServlet = new UserServlet();
    this.userServlet.init(this.servletConfig);
    this.userServlet.setLongIdParserFromRawParameters(this.longIdParserFromRawParameters);
  }

  @AfterEach
  void tearDown() throws Exception {
    autoCloseable.close();
  }

  @Test
  @DisplayName("Проверяет, что если пользователь еще не зарегистрирован, то вызывается метод регистрации")
  void doPost() throws IOException, InvalidUserDataException, UserAlreadyExistsException, NoSuchAlgorithmException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.userServlet.getSignup());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(false);

    this.userServlet.doPost(request, response);


    verify(this.userController, times(1)).createUser(any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_CREATED);
    verify(objectMapper, times(1)).writeValue(any(PrintWriter.class), any());

  }
  @Test
  @DisplayName("Проверяет, что если пользователь еще не зарегистрирован и выпадает ошибка UserAlreadyExistsException, то возвращает статус CONFLICT")
  void doPost_exceptionUserAlreadyExistsException() throws IOException, InvalidUserDataException, UserAlreadyExistsException, NoSuchAlgorithmException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.userServlet.getSignup());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(false);
    when(this.userController.createUser(any())).thenThrow(UserAlreadyExistsException.class);

    this.userServlet.doPost(request, response);


    verify(this.userController, times(1)).createUser(any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_CONFLICT);
    verify(objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что если пользователь еще не зарегистрирован и выпадает ошибка , то возвращает статус BAD_REQUEST")
  void doPost_exceptionInvalidUserDataException() throws IOException, InvalidUserDataException, UserAlreadyExistsException, NoSuchAlgorithmException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.userServlet.getSignup());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(false);
    when(this.userController.createUser(any())).thenThrow(InvalidUserDataException.class);

    this.userServlet.doPost(request, response);


    verify(this.userController, times(1)).createUser(any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что если пользователь уже зарегистрирован , то возвращает статус BAD_REQUEST")
  void doPost_isAuthentication() throws IOException, InvalidUserDataException, UserAlreadyExistsException, NoSuchAlgorithmException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.userServlet.getSignup());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);

    this.userServlet.doPost(request, response);


    verify(this.userController, times(0)).createUser(any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что если пользователь уже зарегистрирован , то возвращает статус BAD_REQUEST")
  void doPost_failedUrl() throws IOException, InvalidUserDataException, UserAlreadyExistsException, NoSuchAlgorithmException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.userServlet.getSignup()+"sdf");
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));

    this.userServlet.doPost(request, response);


    verify(this.userController, times(0)).createUser(any());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
    verify(objectMapper, times(0)).writeValue(any(PrintWriter.class), any());
  }

  @Test
  @DisplayName("Проверяет, что при обращении на данный эндпоинт, вызывается нужный контроллер")
  void doGet() throws IOException, ServletException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.userServlet.getGetUser());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().userId(11L).role(Roles.USER).build());

    this.userServlet.doGet(request, response);

    verify(this.userController, times(1)).getUser(anyLong());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    verify(objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что при обращении на данный эндпоинт с роль админа и передаче параметра userId, вызывается нужный контроллер")
  void doGet_admin_userId() throws IOException, ServletException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.userServlet.getGetUser());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().userId(11L).role(Roles.ADMIN).build());
    when(this.longIdParserFromRawParameters.apply(any())).thenReturn(11L);

    this.userServlet.doGet(request, response);

    verify(this.userController, times(1)).getUser(anyLong());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    verify(objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что при обращении на данный эндпоинт с роль админа и передаче параметра email, вызывается нужный контроллер")
  void doGet_admin_email() throws IOException, ServletException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.userServlet.getGetUser());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    when(request.getParameter( "email")).thenReturn("email");
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().userId(11L).role(Roles.ADMIN).build());

    this.userServlet.doGet(request, response);

    verify(this.userController, times(1)).getUser(anyString());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    verify(objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }
  @Test
  @DisplayName("Проверяет, что при обращении на данный эндпоинт с роль админа и не передавая параметров, возвращается статус BAD_REQUEST")
  void doGet_admin_noParam() throws IOException, ServletException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.userServlet.getGetUser());
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().userId(11L).role(Roles.ADMIN).build());

    this.userServlet.doGet(request, response);

    verify(this.userController, times(0)).getUser(anyLong());
    verify(this.userController, times(0)).getUser(anyString());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(objectMapper, times(1)).writeValue(any(PrintWriter.class), any());
  }

  @Test
  @DisplayName("Проверяет, что при обращении на данный эндпоинт c не известным урл, возвращается статус NOT_FOUND")
  void doGet_admin_noUrl() throws IOException, ServletException {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn(this.userServlet.getGetUser()+"sdf");
    when(request.getReader()).thenReturn(mock(BufferedReader.class));
    var response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(UserSecurity.builder().userId(11L).role(Roles.ADMIN).build());

    this.userServlet.doGet(request, response);

    verify(this.userController, times(0)).getUser(anyLong());
    verify(this.userController, times(0)).getUser(anyString());
    verify(response, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
    verify(objectMapper, times(0)).writeValue(any(PrintWriter.class), any());
  }

  @Test
  @DisplayName("Проверяет, что объект из метода, тот же самый, что и в контексте")
  void getObjectMapper() {
    assertThat(this.userServlet.getObjectMapper()).isEqualTo(this.objectMapper);
  }

}
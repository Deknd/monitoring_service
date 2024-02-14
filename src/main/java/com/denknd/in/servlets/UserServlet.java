package com.denknd.in.servlets;

import com.denknd.config.ManualConfig;
import com.denknd.controllers.UserController;
import com.denknd.dto.UserCreateDto;
import com.denknd.entity.Roles;
import com.denknd.exception.ErrorResponse;
import com.denknd.exception.UserAlreadyExistsException;
import com.denknd.security.service.SecurityService;
import com.denknd.util.functions.LongIdParserFromRawParameters;
import com.denknd.util.impl.Validators;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервлет для работы с пользователями
 */
@WebServlet(name = "UserServlet", urlPatterns = {"/users/*"})
public class UserServlet extends AbstractServlet {
  /**
   * Контроллер для работы с пользователями.
   */
  private UserController userController;
  /**
   * Валидатор для валидации данных входящих.
   */
  private Validators validator;
  /**
   * Сервис по работе с безопасностью
   */
  private SecurityService securityService;
  /**
   * Маппер для преобразовании json в объекты
   */
  private ObjectMapper objectMapper;
  /**
   * функция для преобразования параметра в лонг
   */
  private Function<String, Long> longIdParserFromRawParameters = new LongIdParserFromRawParameters();
  /**
   * Урл для отправки регистрации пользователя в системе
   */
  private final String signup = "/users/signup";
  /**
   * Урл для получения информации о пользователе
   */
  private final String getUser = "/users/user";
  /**
   * Дополнительный параметр, для получения информации о пользователи по емайл.
   */
  private final String EMAIL_PARAM = "email";
  /**
   * Дополнительный параметр, для получения информации о пользователи по айди.
   */
  private final String ID_PARAM = "id";

  /**
   * Инициализация сервлета
   *
   * @param config объект <code>ServletConfig</code>, содержащий конфигурационную информацию для этого сервлета
   * @throws ServletException
   */
  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    var servletContext = config.getServletContext();
    var context = (ManualConfig) servletContext.getAttribute("context");
    this.userController = context.getUserController();
    this.objectMapper = context.getObjectMapper();
    this.validator = context.getValidator();
    this.securityService = context.getSecurityService();
  }

  /**
   * Обработка HTTP POST запросов, для работы с пользователем
   *
   * @param req  объект {@link HttpServletRequest}, содержащий запрос клиента к сервлету
   * @param resp объект {@link HttpServletResponse}, содержащий ответ сервлета клиенту
   * @throws ServletException
   * @throws IOException
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    var requestURI = req.getRequestURI();
    resp.setCharacterEncoding("UTF-8");
    if (requestURI.equals(signup)) {
      if (!this.securityService.isAuthentication()) {
        this.registrationUser(req, resp);
      } else {
        this.responseCreate(resp, new ErrorResponse("У вас уже есть аккаунт."), HttpServletResponse.SC_BAD_REQUEST);
      }
      return;
    }
    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
  }

  /**
   * Метод для регистрации пользователя в системе
   *
   * @param req  входящий запрос от пользователя
   * @param resp ответ собранный для пользователя
   * @throws IOException ошибка при обработки потока информации
   */
  private void registrationUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try (var reader = req.getReader()) {
      var requestBody = reader.lines().collect(Collectors.joining(System.lineSeparator()));
      var userCreateDto = this.objectMapper.readValue(requestBody, UserCreateDto.class);
      validator.validate(userCreateDto);
      var user = this.userController.createUser(userCreateDto);
      this.responseCreate(resp, user, HttpServletResponse.SC_CREATED);
    } catch (UserAlreadyExistsException e) {
      this.responseCreate(resp, new ErrorResponse(e.getMessage()), HttpServletResponse.SC_CONFLICT);
    } catch (Exception e) {
      this.responseCreate(resp, new ErrorResponse(e.getMessage()), HttpServletResponse.SC_BAD_REQUEST);
    }
  }

  /**
   * Обработка HTTP GET запросов получения информации о пользователе по id или email
   *
   * @param req  объект {@link HttpServletRequest}, содержащий запрос клиента к сервлету
   * @param resp объект {@link HttpServletResponse}, содержащий ответ сервлета клиенту
   * @throws ServletException
   * @throws IOException
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    var requestURI = req.getRequestURI();
    resp.setCharacterEncoding("UTF-8");
    if (requestURI.equals(this.getUser) && this.securityService.isAuthentication()) {
      var userSecurity = this.securityService.getUserSecurity();
      if (userSecurity.role().equals(Roles.USER)) {
        var user = this.userController.getUser(userSecurity.userId());
        this.responseCreate(resp, user, HttpServletResponse.SC_OK);
        return;
      }
      if (userSecurity.role().equals(Roles.ADMIN)) {
        var userId = this.longIdParserFromRawParameters.apply(req.getParameter(this.ID_PARAM));
        if (userId != null) {
          var user = this.userController.getUser(userId);
          this.responseCreate(resp, user, HttpServletResponse.SC_OK);
          return;
        }
        var email = req.getParameter(this.EMAIL_PARAM);
        if (email != null) {
          var user = this.userController.getUser(email);
          this.responseCreate(resp, user, HttpServletResponse.SC_OK);
          return;
        }
      }
      this.responseCreate(
              resp,
              new ErrorResponse(
                      "Нет обязательного парамметра для получения запроса о других пользователей." +
                              " Добавьте к запросу параметр: " + this.ID_PARAM + "={userId} или " + this.EMAIL_PARAM
                              + "=email@пользователя.com"), HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
  }

  /**
   * Объект для маппинга объектов в json и обратно
   *
   * @return Объект для маппинга объектов в json и обратно
   */
  @Override
  protected ObjectMapper getObjectMapper() {
    return this.objectMapper;
  }

  /**
   * Урл для регистрации пользователя
   *
   * @return урл в виде строки
   */
  public String getSignup() {
    return this.signup;
  }

  /**
   * Урл для получения информации о пользователе
   *
   * @return урл в виде строки
   */
  public String getGetUser() {
    return getUser;
  }

  /**
   * функция для парсинга числа из параметра
   *
   * @param longIdParserFromRawParameters функция которая принимает строку, выводит число
   */
  public void setLongIdParserFromRawParameters(Function<String, Long> longIdParserFromRawParameters) {
    this.longIdParserFromRawParameters = longIdParserFromRawParameters;
  }
}

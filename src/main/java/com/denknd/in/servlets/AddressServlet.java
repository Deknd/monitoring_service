package com.denknd.in.servlets;

import com.denknd.config.ManualConfig;
import com.denknd.controllers.AddressController;
import com.denknd.dto.AddressDto;
import com.denknd.entity.Roles;
import com.denknd.exception.ErrorResponse;
import com.denknd.security.entity.UserSecurity;
import com.denknd.security.service.SecurityService;
import com.denknd.util.functions.LongIdParserFromRawParameters;
import com.denknd.util.impl.Validators;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервлет для приема запросов на обработку данных связаных с адресом
 */
@WebServlet(name = "AddressServlet", urlPatterns = {"/address"})
@Slf4j
public class AddressServlet extends AbstractServlet {
  /**
   * Маппер объектов в json и на оборот
   */
  private ObjectMapper objectMapper;
  /**
   * Валидатор данных.
   */
  private Validators validator;
  /**
   * Сервис по работе с безопасностью
   */
  private SecurityService securityService;
  /**
   * Контроллер для работы с адресами
   */
  private AddressController addressController;
  /**
   * Функция для преобразования строки в цифру
   */
  private Function<String, Long> longIdParserFromRawParameters = new LongIdParserFromRawParameters();
  /**
   * Адрес для приема запросов на добавления нового адреса
   */
  private String addAddress = "/address";
  /**
   * Адрес для получения информации о существующих адресах
   */
  private String getAddress = "/address";
  /**
   * Дополнительный параметр, для получения информации о пользователи по айди.
   */
  private final String paramUserId = "userId";

  /**
   * Инициализация сервлета
   *
   * @param config объект <code>ServletConfig</code>, содержащий конфигурационную информацию для этого сервлета
   * @throws ServletException ошибка сервлета
   */
  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    var servletContext = config.getServletContext();
    var context = (ManualConfig) servletContext.getAttribute("context");
    this.objectMapper = context.getObjectMapper();
    this.validator = context.getValidator();
    this.securityService = context.getSecurityService();
    this.addressController = context.getAddressController();
  }

  /**
   * Обработка HTTP POST запросов, таких как добавления нового адреса.
   *
   * @param req  объект {@link HttpServletRequest}, содержащий запрос клиента к сервлету
   * @param resp объект {@link HttpServletResponse}, содержащий ответ сервлета клиенту
   * @throws ServletException ошибка сервлета
   * @throws IOException      ошибка при работе с потоком даных
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    var requestURI = req.getRequestURI();
    resp.setCharacterEncoding("UTF-8");
    if (requestURI.equals(this.addAddress)) {
      var userSecurity = this.securityService.getUserSecurity();
      if (this.securityService.isAuthentication() && userSecurity.role().equals(Roles.USER)) {
        addAddress(req, resp, userSecurity);
        return;
      }
    }
    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
  }

  /**
   * Добавления адреса пользователю
   *
   * @param req          объект {@link HttpServletRequest}, содержащий запрос клиента к сервлету
   * @param resp         объект {@link HttpServletResponse}, содержащий ответ сервлета клиенту
   * @param userSecurity пользователь из системы безопасности
   * @throws IOException ошибка при обработке потока данных
   */
  private void addAddress(HttpServletRequest req, HttpServletResponse resp, UserSecurity userSecurity) throws IOException {
    try (var reader = req.getReader()) {
      var requestBody = reader.lines().collect(Collectors.joining(System.lineSeparator()));
      var addressDto = this.objectMapper.readValue(requestBody, AddressDto.class);
      this.validator.validate(addressDto);
      var result = this.addressController.addAddress(addressDto, userSecurity.userId());
      responseCreate(resp, result, HttpServletResponse.SC_CREATED);
    } catch (Exception e) {
      log.error(e.getMessage());
      this.responseCreate(resp, new ErrorResponse(e.getMessage()), HttpServletResponse.SC_BAD_REQUEST);
    }
  }

  /**
   * Обработка HTTP GET запросов получения информации о адресах пользователя
   *
   * @param req  объект {@link HttpServletRequest}, содержащий запрос клиента к сервлету
   * @param resp объект {@link HttpServletResponse}, содержащий ответ сервлета клиенту
   * @throws IOException ошибка при работе с потоком данных
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    var requestURI = req.getRequestURI();
    resp.setCharacterEncoding("UTF-8");
    if (requestURI.equals(this.getAddress)) {
      var userSecurity = this.securityService.getUserSecurity();
      if (this.securityService.isAuthentication()) {
        if (userSecurity.role().equals(Roles.USER)) {
          var addresses = this.addressController.getAddress(userSecurity.userId());
          this.responseCreate(resp, addresses, HttpServletResponse.SC_OK);
          return;
        }
        if (userSecurity.role().equals(Roles.ADMIN)) {
          var userId = this.longIdParserFromRawParameters.apply(req.getParameter(this.paramUserId));
          if (userId == null) {
            this.responseCreate(
                    resp,
                    new ErrorResponse("Не передан основной параметр запроса(пример: " + this.paramUserId + "=11 )"),
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
          }
          var address = this.addressController.getAddress(userId);
          this.responseCreate(resp, address, HttpServletResponse.SC_OK);
          return;
        }
      }
    }
    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
  }

  /**
   * Получение объекта ObjectMapper
   *
   * @return объект ObjectMapper
   */
  @Override
  protected ObjectMapper getObjectMapper() {
    return this.objectMapper;
  }

  /**
   * Измеения функции получения из параметоров айди
   *
   * @param longIdParserFromRawParameters функция для получения из параметров идентификатора
   */
  public void setLongIdParserFromRawParameters(Function<String, Long> longIdParserFromRawParameters) {
    this.longIdParserFromRawParameters = longIdParserFromRawParameters;
  }

  /**
   * Выдает адрес для пост запроса
   *
   * @return адрес для пост запроса
   */
  public String getAddAddress() {
    return addAddress;
  }

  /**
   * Настройка адреса для пост запроса
   *
   * @param addAddress адрес для пост запроса
   */
  public void setAddAddress(String addAddress) {
    this.addAddress = addAddress;
  }

  /**
   * выдает адрес для гет запроса о информации об адресах
   *
   * @return адрес
   */
  public String getGetAddress() {
    return getAddress;
  }

  /**
   * Настрока адреса для гет запроса на получения информации об адресах
   *
   * @param getAddress адрес
   */
  public void setGetAddress(String getAddress) {
    this.getAddress = getAddress;
  }

  /**
   * Параметр для запроса адреса по айди пользователя доступный для админа
   *
   * @return возвращает название параметра для получения информации об айди
   */
  public String getParamUserId() {
    return paramUserId;
  }
}

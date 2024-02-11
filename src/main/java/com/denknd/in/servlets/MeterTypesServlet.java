package com.denknd.in.servlets;

import com.denknd.config.ManualConfig;
import com.denknd.controllers.TypeMeterController;
import com.denknd.dto.TypeMeterDto;
import com.denknd.entity.Roles;
import com.denknd.exception.ErrorResponse;
import com.denknd.security.service.SecurityService;
import com.denknd.util.impl.Validators;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Сервлет для обработки запросов по типу параметров
 */
@WebServlet(name = "MeterTypesServlet", urlPatterns = "/meter-types")
public class MeterTypesServlet extends AbstractServlet {
  /**
   * Сервис для работы с безопасностью
   */
  private SecurityService securityService;
  /**
   * Объект для маппинг в json и обратно
   */
  private ObjectMapper objectMapper;
  /**
   * Валидатор входящих данных
   */
  private Validators validator;
  /**
   * Контроллер для работы с типами показаний
   */
  private TypeMeterController typeMeterController;
  /**
   * Урл для работы с этим сервлетом.
   */
  private final String meterTypesPatch = "/meter-types";

  /**
   * Инициализация сервлета
   * @param config объект <code>ServletConfig</code>, содержащий конфигурационную информацию для этого сервлета
   * @throws ServletException
   */
  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    var servletContext = config.getServletContext();
    var context = (ManualConfig) servletContext.getAttribute("context");
    this.objectMapper = context.getObjectMapper();
    this.securityService = context.getSecurityService();
    this.validator = context.getValidator();
    this.typeMeterController = context.getTypeMeterController();
  }

  /**
   * Обработка HTTP POST запросов, таких как добавления новых типов показаний
   * @param req  объект {@link HttpServletRequest}, содержащий запрос клиента к сервлету
   * @param resp объект {@link HttpServletResponse}, содержащий ответ сервлета клиенту
   * @throws ServletException
   * @throws IOException
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    var requestURI = req.getRequestURI();
    if (requestURI.equals(meterTypesPatch)) {
      var userSecurity = this.securityService.getUserSecurity();
      if (this.securityService.isAuthentication() && userSecurity.role().equals(Roles.ADMIN)) {
        try (var reader = req.getReader()) {
          var requestBody = reader.lines().collect(Collectors.joining(System.lineSeparator()));
          var typeMeterDto = this.objectMapper.readValue(requestBody, TypeMeterDto.class);
          this.validator.validate(typeMeterDto);
          var result = this.typeMeterController.addNewType(typeMeterDto);
          responseCreate(resp, result, HttpServletResponse.SC_CREATED);
          return;
        } catch (Exception e) {
          this.responseCreate(resp, new ErrorResponse(e.getMessage()), HttpServletResponse.SC_BAD_REQUEST);
          return;
        }

      }
      this.responseCreate(
              resp,
              new ErrorResponse("Нужно авторизоваться c ролью ADMIN. Данный эндпоинт доступен только пользователям с ролью ADMIN"),
              HttpServletResponse.SC_FORBIDDEN);
      return;
    }
    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
  }

  /**
   * Обработка HTTP GET запросов получения информации о доступных типах показаний
   * @param req  объект {@link HttpServletRequest}, содержащий запрос клиента к сервлету
   * @param resp объект {@link HttpServletResponse}, содержащий ответ сервлета клиенту
   * @throws ServletException
   * @throws IOException
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    var requestURI = req.getRequestURI();
    if (requestURI.equals(this.meterTypesPatch)) {
      if (this.securityService.isAuthentication()) {
        var typeMeterCodes = this.typeMeterController.getTypeMeterCodes();
        this.responseCreate(resp, typeMeterCodes, HttpServletResponse.SC_OK);
        return;
      }
      this.responseCreate(
              resp,
              new ErrorResponse("Нужно авторизоваться. Данный эндпоинт доступен только авторизированным пользователям"),
              HttpServletResponse.SC_FORBIDDEN);
      return;
    }
    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
  }

  /**
   * Объект для маппинга объектов в json и обратно
   * @return Объект для маппинга объектов в json и обратно
   */
  @Override
  protected ObjectMapper getObjectMapper() {
    return this.objectMapper;
  }
  /**
   * Урл для работы с данном сервлетом
   * @return урл для работы с данном сервлетом
   */
  public String getMeterTypesPatch() {
    return meterTypesPatch;
  }
}

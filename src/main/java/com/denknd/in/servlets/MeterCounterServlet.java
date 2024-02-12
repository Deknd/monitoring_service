package com.denknd.in.servlets;

import com.denknd.config.ManualConfig;
import com.denknd.controllers.CounterInfoController;
import com.denknd.dto.CounterInfoDto;
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
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Сервлет для получения запросов, для работы с счетчиками
 */
@Log4j2
@WebServlet(name = "MeterCounterServlet", urlPatterns = "/counter-info")
public class MeterCounterServlet extends AbstractServlet {
  /**
   * Маппер объектов в json и обратно
   */
  private ObjectMapper objectMapper;
  /**
   * Сервис для работы с безопасностью.
   */
  private SecurityService securityService;
  /**
   * Валидатор входных данных
   */
  private Validators validator;
  /**
   * Контроллер по работе со счетчиками
   */
  private CounterInfoController counterInfoController;
  /**
   * Урл для обращения к данному сервлету
   */
  private final String meterCounterPatch = "/counter-info";

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
    this.counterInfoController = context.getCounterInfoController();
  }

  /**
   * Обработка HTTP POST запросов, таких как добавления информации по счетчику.
   * @param req  объект {@link HttpServletRequest}, содержащий запрос клиента к сервлету
   * @param resp объект {@link HttpServletResponse}, содержащий ответ сервлета клиенту
   * @throws ServletException
   * @throws IOException
   */
  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    var requestURI = req.getRequestURI();
    if (requestURI.equals(meterCounterPatch)) {
      var userSecurity = securityService.getUserSecurity();
      if (this.securityService.isAuthentication() && userSecurity.role().equals(Roles.ADMIN)) {
        try (var reader = req.getReader()) {
          var requestBody = reader.lines().collect(Collectors.joining(System.lineSeparator()));
          var counterInfoDto = this.objectMapper.readValue(requestBody, CounterInfoDto.class);
          this.validator.validate(counterInfoDto);
          var result = this.counterInfoController.addInfoForMeter(counterInfoDto);
          this.responseCreate(resp, result, HttpServletResponse.SC_OK);
          return;
        } catch (Exception e) {
          log.error(e.getMessage());
          this.responseCreate(resp, new ErrorResponse(e.getMessage()), HttpServletResponse.SC_BAD_REQUEST);
          return;
        }
      }
    }
    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
  }

  /**
   * Возвращает объект для маппинга json в объекты и на оборот
   * @return
   */
  @Override
  protected ObjectMapper getObjectMapper() {
    return this.objectMapper;
  }

  /**
   * Получения урла по которому доступна данный сервлет
   * @return
   */
  public String getMeterCounterPatch() {
    return meterCounterPatch;
  }


}

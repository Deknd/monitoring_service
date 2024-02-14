package com.denknd.in.servlets;

import com.denknd.aspectj.time.MeasureExecutionTime;
import com.denknd.config.ManualConfig;
import com.denknd.controllers.MeterReadingController;
import com.denknd.dto.MeterReadingRequestDto;
import com.denknd.entity.Roles;
import com.denknd.exception.ConstraintViolationException;
import com.denknd.exception.ErrorResponse;
import com.denknd.exception.MeterReadingConflictError;
import com.denknd.security.entity.UserSecurity;
import com.denknd.security.service.SecurityService;
import com.denknd.util.functions.DateParserFromRawParameters;
import com.denknd.util.functions.LongIdParserFromRawParameters;
import com.denknd.util.functions.TypeMeterParametersParserFromRawParameters;
import com.denknd.util.impl.Validators;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.YearMonth;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервлет для получения и добавления информации по показаниям
 */
@Slf4j

@WebServlet(name = "MeterReadingsServlet", urlPatterns = {"/meter-readings/*"})
public class MeterReadingsServlet extends AbstractServlet {
  /**
   * Маппер для маппинга объектов в json и обратно
   */
  private ObjectMapper objectMapper;
  /**
   * Контроллер для работы с показаниями.
   */
  private MeterReadingController meterReadingController;
  /**
   * Валидатор входных данных
   */
  private Validators validator;
  /**
   * Сервис для работы с безопасностью.
   */
  private SecurityService securityService;
  /**
   * Функция преобразования последовательности параметров айди типов в  коллекцию айди типов.
   */
  private Function<String, Set<Long>> typeMeterParametersParserFromRawParameters;
  /**
   * Функция преобразования параметра в лонг
   */
  private Function<String, Long> longIdParserFromRawParameters = new LongIdParserFromRawParameters();
  /**
   * Функция по преобразованию параметра в дату
   */
  private Function<String, YearMonth> dateParserFromRawParameter = new DateParserFromRawParameters();
  /**
   * Урл для отправки показаний
   */
  private final String sendMeterReading = "/meter-readings/send";
  /**
   * Урл для получения истории показаний
   */
  private final String historyMeterReading = "/meter-readings/history";
  /**
   * Урл для получения актуальный показаний или показаний по определенной дате
   */
  private final String getMeterReadings = "/meter-readings/get-meter-readings";
  /**
   * Параметр для передачи информации, о типах показаний
   */
  private final String paramTypeId = "typeId";
  /**
   * Параметр для передачи информации, о адресе пользователя
   */
  private final String paramAddressId = "addrId";
  /**
   * Параметр для передачи информации, о идентификаторе пользователя
   */
  private final String paramUserId = "userId";

  /**
   * Дополнительный параметр для передачи даты начала списка.
   */
  private final String paramStartDate = "start_date";
  /**
   * Дополнительный параметр для передачи даты конца списка.
   */
  private final String paramEndDate = "end_date";
  /**
   * Параметр для передачи информации о дате
   */
  private final String paramDate = "data";

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
    this.meterReadingController = context.getMeterReadingController();
    this.securityService = context.getSecurityService();
    this.validator = context.getValidator();
    if (this.typeMeterParametersParserFromRawParameters == null) {
      this.typeMeterParametersParserFromRawParameters
              = new TypeMeterParametersParserFromRawParameters(
              context.getTypeMeterController());
    }
  }

  /**
   * Обработка HTTP POST запросов, таких как добавления новых показаний.
   *
   * @param req  объект {@link HttpServletRequest}, содержащий запрос клиента к сервлету
   * @param resp объект {@link HttpServletResponse}, содержащий ответ сервлета клиенту
   * @throws IOException ошибка при работе с потоком данных
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    var requestURI = req.getRequestURI();
    resp.setCharacterEncoding("UTF-8");
    if (requestURI.equals(sendMeterReading)) {
      var userSecurity = this.securityService.getUserSecurity();
      if (this.securityService.isAuthentication() && userSecurity.role().equals(Roles.USER)) {
        sendMeterReadings(req, resp, userSecurity);
        return;
      }
      this.responseCreate(
              resp,
              new ErrorResponse("Нужно авторизоваться. Данный эндпоинт доступен только пользователям с ролью USER"),
              HttpServletResponse.SC_FORBIDDEN);
      return;
    }
    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
  }

  /**
   * Метод для добавления показаний счетчика.
   *
   * @param req          входящий запрос от пользователя
   * @param resp         ответ пользователю
   * @param userSecurity авторизированный пользоавтель
   * @throws IOException ошибка при обрыве соединения
   */
  @MeasureExecutionTime
  private void sendMeterReadings(HttpServletRequest req, HttpServletResponse resp, UserSecurity userSecurity) throws IOException {
    try (var reader = req.getReader()) {
      var requestBody = reader.lines().collect(Collectors.joining(System.lineSeparator()));
      var meterReadingRequestDto
              = this.objectMapper.readValue(requestBody, MeterReadingRequestDto.class);
      try {
        this.validator.validate(meterReadingRequestDto);
        var meterReadingResponseDto
                = this.meterReadingController.addMeterReadingValue(meterReadingRequestDto, userSecurity.userId());
        this.responseCreate(resp, meterReadingResponseDto, HttpServletResponse.SC_CREATED);
      } catch (MeterReadingConflictError e) {
        log.error(e.getMessage());
        this.responseCreate(
                resp,
                new ErrorResponse("Ошибка сохранения показаний. " + e.getMessage()),
                HttpServletResponse.SC_CONFLICT
        );
      } catch (ConstraintViolationException e) {
        this.responseCreate(resp, new ErrorResponse(e.getMessage()), HttpServletResponse.SC_BAD_REQUEST);
      }
    }
  }

  /**
   * Обработка HTTP GET запросов получения историии показаний или актуальных показаний
   *
   * @param req  объект {@link HttpServletRequest}, содержащий запрос клиента к сервлету
   * @param resp объект {@link HttpServletResponse}, содержащий ответ сервлета клиенту
   * @throws IOException ошибка при работе с потоком данных
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    var requestURI = req.getRequestURI();
    resp.setCharacterEncoding("UTF-8");
    var userSecurity = this.securityService.getUserSecurity();
    if (requestURI.equals(this.historyMeterReading) && this.securityService.isAuthentication()) {
      getHistory(req, resp, userSecurity);
      return;
    }
    if (requestURI.equals(this.getMeterReadings) && this.securityService.isAuthentication()) {
      getMeterValues(req, resp, userSecurity);
      return;
    }
    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
  }

  /**
   * Получения актуальных показаний
   *
   * @param req          входящий запрос пользователя
   * @param resp         ответ пользователю
   * @param userSecurity авторизированный пользователь
   * @throws IOException ошибка соединения
   */
  @MeasureExecutionTime
  private void getMeterValues(HttpServletRequest req, HttpServletResponse resp, UserSecurity userSecurity) throws IOException {
    var acceptedParameters = this.typeMeterParametersParserFromRawParameters.apply(req.getParameter(this.paramTypeId));
    var dateFilter = this.dateParserFromRawParameter.apply(req.getParameter(this.paramDate));
    var addressId = this.longIdParserFromRawParameters.apply(req.getParameter(this.paramAddressId));
    if (userSecurity.role().equals(Roles.USER)) {
      var meterReadings =
              this.meterReadingController.getMeterReadings(
                      addressId,
                      userSecurity.userId(),
                      acceptedParameters,
                      dateFilter);
      this.responseCreate(resp, meterReadings, HttpServletResponse.SC_OK);
      return;
    }
    if (userSecurity.role().equals(Roles.ADMIN)) {
      var userId = this.longIdParserFromRawParameters.apply(req.getParameter(this.paramUserId));
      if (userId == null) {
        this.responseCreate(
                resp,
                new ErrorResponse("Вы не ввели айди в обязательный параметр для Администратора, при вызове этого эндроинта"),
                HttpServletResponse.SC_BAD_REQUEST
        );
        return;
      }
      var meterReadings =
              this.meterReadingController.getMeterReadings(
                      addressId,
                      userId,
                      acceptedParameters,
                      dateFilter);
      this.responseCreate(resp, meterReadings, HttpServletResponse.SC_OK);
      return;
    }
  }

  /**
   * Метод для обработки запроса по выдачи истории показаний
   *
   * @param req          входящий запрос пользователя
   * @param resp         ответ для пользователя
   * @param userSecurity авторизированнный пользователь
   * @throws IOException ошибка соединения
   */
  @MeasureExecutionTime
  private void getHistory(HttpServletRequest req, HttpServletResponse resp, UserSecurity userSecurity) throws IOException {
    var acceptedParameters = this.typeMeterParametersParserFromRawParameters.apply(req.getParameter(this.paramTypeId));
    var addressId = this.longIdParserFromRawParameters.apply(req.getParameter(this.paramAddressId));
    var startDate = this.dateParserFromRawParameter.apply(req.getParameter(this.paramStartDate));
    var endDate = this.dateParserFromRawParameter.apply(req.getParameter(this.paramEndDate));
    if (userSecurity.role().equals(Roles.USER)) {
      var meterReadingResponseDtos = this.meterReadingController.getHistoryMeterReading(
              addressId,
              userSecurity.userId(),
              acceptedParameters,
              startDate,
              endDate
      );
      this.responseCreate(resp, meterReadingResponseDtos, HttpServletResponse.SC_OK);
      return;
    }
    if (userSecurity.role().equals(Roles.ADMIN)) {
      var userId = this.longIdParserFromRawParameters.apply(req.getParameter(this.paramUserId));
      if (userId == null) {
        this.responseCreate(
                resp,
                new ErrorResponse("Вы не ввели айди в обязательный параметр для Администратора, при вызове этого эндроинта"),
                HttpServletResponse.SC_BAD_REQUEST
        );
        return;
      }
      var meterReadingResponseDtos = this.meterReadingController.getHistoryMeterReading(
              addressId,
              userId,
              acceptedParameters,
              startDate,
              endDate
      );
      this.responseCreate(resp, meterReadingResponseDtos, HttpServletResponse.SC_OK);
    }
  }

  /**
   * Возвращает обхект для маппинга в json и обратно
   *
   * @return обхект для маппинга в json и обратно
   */
  @Override
  protected ObjectMapper getObjectMapper() {
    return this.objectMapper;
  }

  /**
   * Урл для отправки показаний
   *
   * @return урл для отправки показаний
   */
  public String getSendMeterReading() {
    return sendMeterReading;
  }

  /**
   * Урл для получения истории показаний
   *
   * @return урл для получения истории
   */
  public String getHistoryMeterReading() {
    return historyMeterReading;
  }

  /**
   * Урл для получения актуальных показаний
   *
   * @return урл для получения показаний
   */
  public String getGetMeterReadings() {
    return getMeterReadings;
  }

  /**
   * Настройка для получения функции по парсингу параметров
   *
   * @param typeMeterParametersParserFromRawParameters функция для парсинга параметров из строки
   */
  public void setTypeMeterParametersParserFromRawParameters(Function<String, Set<Long>> typeMeterParametersParserFromRawParameters) {
    this.typeMeterParametersParserFromRawParameters = typeMeterParametersParserFromRawParameters;
  }

  /**
   * Настрока для функции по получению из параметров числа
   *
   * @param longIdParserFromRawParameters функция для получения из параметров числа
   */
  public void setLongIdParserFromRawParameters(Function<String, Long> longIdParserFromRawParameters) {
    this.longIdParserFromRawParameters = longIdParserFromRawParameters;
  }

  /**
   * Добавления функции для получения из параметров даты
   *
   * @param dateParserFromRawParameter функция для получения из параметров даты
   */
  public void setDateParserFromRawParameter(Function<String, YearMonth> dateParserFromRawParameter) {
    this.dateParserFromRawParameter = dateParserFromRawParameter;
  }
}

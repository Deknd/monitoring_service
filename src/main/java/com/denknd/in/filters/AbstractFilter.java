package com.denknd.in.filters;

import com.denknd.exception.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

/**
 * Абстрактный класс фильтра, предоставляющий базовую реализацию для других фильтров.
 * Обеспечивает установку ответа на исключение в формате JSON при возникновении ошибок.
 */
@Log4j2
@RequiredArgsConstructor
public abstract class AbstractFilter  extends HttpFilter {
  /**
   * Для маппинга в джесон и обратно
   */
  protected abstract ObjectMapper getObjectMapper();
  /**
   * Метод фильтрации запросов.
   * Переопределяет метод doFilter из HttpFilter.
   * Данный метод вызывает цепочку следующих фильтров или ресурсов.
   * @param req   объект HttpServletRequest, содержащий запрос клиента
   * @param res   объект HttpServletResponse, содержащий ответ фильтра для клиента
   * @param chain объект FilterChain для вызова следующего фильтра или ресурса
   * @throws IOException
   * @throws ServletException
   */
  @Override
  protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
    super.doFilter(req, res, chain);
  }
  /**
   * Устанавливает ответ на исключение в формате JSON.
   * Принимает объект HttpServletResponse, сообщение об ошибке и код статуса.
   * Преобразует сообщение об ошибке в формат JSON с помощью объекта ObjectMapper,
   * устанавливает соответствующие заголовки ответа и записывает JSON-ответ в поток вывода.
   * @param httpResponse объект HttpServletResponse для установки ответа
   * @param errorMessage сообщение об ошибке
   * @param status код статуса ответа
   * @throws JsonProcessingException при ошибке преобразования в JSON
   */
  protected void setExceptionResponse(HttpServletResponse httpResponse, String errorMessage, int status) throws JsonProcessingException {
    var jsonResponse = this.getObjectMapper().writeValueAsString(new ErrorResponse(errorMessage));
    httpResponse.setContentType("application/json");
    httpResponse.setStatus(status);
    httpResponse.setCharacterEncoding("UTF-8");
    try (var writer = httpResponse.getWriter()) {
      writer.write(jsonResponse);
    } catch (IOException ex) {
      log.error(ex.getMessage());
    }
  }
}

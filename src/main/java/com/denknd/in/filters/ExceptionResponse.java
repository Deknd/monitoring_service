package com.denknd.in.filters;

import com.denknd.exception.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * Интерфейс для обработки исключений и установки ответа в формате JSON.
 */
public interface ExceptionResponse {
  /**
   * Получает экземпляр ObjectMapper для преобразования объектов в JSON.
   *
   * @return Объект ObjectMapper.
   */
  ObjectMapper getObjectMapper();

  /**
   * Получает экземпляр логгера для записи логов.
   *
   * @return Экземпляр логгера.
   */
  Logger logger();

  /**
   * Устанавливает ответ на исключение в формате JSON.
   * Преобразует сообщение об ошибке в формат JSON с помощью объекта ObjectMapper,
   * устанавливает соответствующие заголовки ответа и записывает JSON-ответ в поток вывода.
   *
   * @param httpResponse объект HttpServletResponse для установки ответа
   * @param errorMessage сообщение об ошибке
   * @param status       код статуса ответа
   * @throws JsonProcessingException если произошла ошибка при преобразовании в JSON
   */
  default void setExceptionResponse(HttpServletResponse httpResponse, String errorMessage, HttpStatus status) throws JsonProcessingException {
    var jsonResponse = this.getObjectMapper().writeValueAsString(new ErrorResponse(errorMessage));
    httpResponse.setContentType("application/json");
    httpResponse.setStatus(status.value());
    httpResponse.setCharacterEncoding("UTF-8");
    try (var writer = httpResponse.getWriter()) {
      writer.write(jsonResponse);
    } catch (IOException ex) {
      logger().error(ex.getMessage());
    }
  }
}

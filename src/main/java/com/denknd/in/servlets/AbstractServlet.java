package com.denknd.in.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * Расширяет класс HttpServlet
 */
public abstract class AbstractServlet extends HttpServlet {
  /**
   * Метод для маппинга объектов в Json
   * @return объект который маппит объекты в Json
   */
  protected abstract ObjectMapper getObjectMapper();

  /**
   * Добавляет в ответ переданный статус и объект
   * @param resp ответ, который уйдет пользователю
   * @param object объект, который нужно добавить в ответ
   * @param status статус, который будет отправлен пользователю
   * @param <T> тип объекта, который передан в метод
   * @throws IOException ошибка при сериализации объекта
   */
  protected  <T> void responseCreate(HttpServletResponse resp, T object, int status) throws IOException {
    resp.setStatus(status);
    try (var writer = resp.getWriter()) {
      this.getObjectMapper().writeValue(writer, object);
    }
  }
}

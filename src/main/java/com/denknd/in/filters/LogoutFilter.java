package com.denknd.in.filters;

import com.denknd.security.service.SecurityService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Фильтр для блокировки токена доступа.
 * Этот фильтр предназначен для обработки запросов на выход из системы (логаут).
 */
@Getter
@RequiredArgsConstructor
@Component
public class LogoutFilter extends HttpFilter {
  /**
   * Паттерн URI для вызова данного фильтра.
   */
  private final String URL_PATTERNS = "/auth/logout";
  /**
   * Метод HTTP для вызова данного фильтра.
   */
  private final String HTTP_METHOD = "POST";
  /**
   * Сервис для работы с безопасностью.
   */
  private final SecurityService securityService;


  /**
   * Метод фильтрации запросов.
   * Если URI запроса соответствует URL_PATTERNS и метод запроса равен HTTP_METHOD,
   * то фильтр выполняет процедуру выхода из системы (логаут).
   * Для этого вызывается метод logout() из securityService, который обрабатывает
   * выход пользователя из системы.
   * После успешного выполнения логаута устанавливается статус ответа SC_NO_CONTENT,
   * что указывает на успешное выполнение операции без возврата содержимого.
   * В противном случае, запрос передается по цепочке фильтров.
   *
   * @param req   объект HttpServletRequest, содержащий запрос клиента
   * @param res   объект HttpServletResponse, содержащий ответ фильтра для клиента
   * @param chain объект FilterChain для вызова следующего фильтра или ресурса
   * @throws IOException      ошибка ввода-вывода
   * @throws ServletException ошибка сервлета
   */
  @Override
  protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
          throws IOException, ServletException {
    var requestURI = req.getRequestURI();
    var method = req.getMethod();
    if (requestURI.equalsIgnoreCase(this.URL_PATTERNS) && method.equals(this.HTTP_METHOD)) {
      this.securityService.logout(res);
      res.setStatus(HttpServletResponse.SC_NO_CONTENT);
      return;
    }
    super.doFilter(req, res, chain);
  }
}

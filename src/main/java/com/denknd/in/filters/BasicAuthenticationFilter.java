package com.denknd.in.filters;

import com.denknd.exception.BadCredentialsException;
import com.denknd.security.service.SecurityService;
import com.denknd.security.utils.authenticator.UserAuthenticator;
import com.denknd.security.utils.converter.AuthenticationConverter;
import com.denknd.security.utils.converter.impl.BasicAuthenticationConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Фильтр для получения токена доступа
 */
@Setter
@Slf4j
public class BasicAuthenticationFilter extends AbstractFilter {
  /**
   * Паттерн по которому срабатывает фильтр
   */
  private String URL_PATTERNS = "/auth/login";
  /**
   * Сервис по работе с безопасностью
   */
  private final SecurityService securityService;
  /**
   * Служит для аутентификации пользователя по логину и паролю
   */
  private final UserAuthenticator userAuthenticator;
  /**
   * Для маппинга в джесон и обратно
   */
  private ObjectMapper objectMapper;
  /**
   * Получает объект пре аутентификации из полученного от пользователя запроса
   */
  private AuthenticationConverter authenticationConverter = new BasicAuthenticationConverter();


  public BasicAuthenticationFilter(ObjectMapper objectMapper, SecurityService securityService, UserAuthenticator userAuthenticator) {
    this.securityService = securityService;
    this.userAuthenticator = userAuthenticator;
    this.objectMapper = objectMapper;
  }


  /**
   * Для маппинга в Json и обратно
   *
   * @return объект для работы с Json
   */
  @Override
  protected ObjectMapper getObjectMapper() {
    return this.objectMapper;
  }

  /**
   * Метод фильтрации запросов.
   * Если метод запроса POST и URI запроса соответствует URL_PATTERNS,
   * то фильтр пытается выполнить аутентификацию пользователя.
   * Если аутентификация проходит успешно, пользователь добавляется в контекст безопасности,
   * и вызывается метод onAuthentication() из securityService для настройки безопасности в ответе.
   * Если запрос на аутентификацию не содержит необходимых данных,
   * или аутентификация не проходит, возвращается соответствующий статус ответа.
   * В противном случае, запрос передается по цепочке фильтров.
   *
   * @param httpRequest  объект HttpServletRequest, содержащий запрос клиента
   * @param httpResponse объект HttpServletResponse, содержащий ответ фильтра для клиента
   * @param chain        объект FilterChain для вызова следующего фильтра или ресурса
   * @throws IOException ошибка при работе с потоком данных
   * @throws ServletException ошибка сервлета
   */
  @Override
  protected void doFilter(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain chain) throws IOException, ServletException {
    if (httpRequest.getMethod().equalsIgnoreCase("POST") && httpRequest.getRequestURI().equals(URL_PATTERNS)) {
      try {
        var preAuthenticated = this.authenticationConverter.convert(httpRequest);
        if (preAuthenticated == null) {
          var errorMessage = "Запрос на аутентификацию не обработан, так как не удалось найти имя пользователя и пароль в заголовке базовой авторизации";
          setExceptionResponse(httpResponse, errorMessage, HttpServletResponse.SC_UNAUTHORIZED);
          return;
        }
        var authentication = this.userAuthenticator.authentication(preAuthenticated);
        if (authentication == null) {
          var errorMessage = "Запрос на аутентификацию не обработан, так как логин или пароль не верны";
          setExceptionResponse(httpResponse, errorMessage, HttpServletResponse.SC_UNAUTHORIZED);
          return;
        }
        this.securityService.addPrincipal(authentication);
        this.securityService.onAuthentication(httpResponse);
        httpResponse.setStatus(HttpServletResponse.SC_OK);
        return;
      } catch (BadCredentialsException e) {
        log.error("Ошибка авторизации. " + e.getMessage());
        setExceptionResponse(httpResponse, e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }
    }
    super.doFilter(httpRequest, httpResponse, chain);
  }

  /**
   * Паттерн на который срабатывает фильтр
   *
   * @return паттер урла данного фильтра
   */
  public String getURL_PATTERNS() {
    return URL_PATTERNS;
  }
}

package com.denknd.in.filters;

import com.denknd.exception.BadCredentialsException;
import com.denknd.security.service.SecurityService;
import com.denknd.security.utils.authenticator.UserAuthenticator;
import com.denknd.security.utils.converter.AuthenticationConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Фильтр для авторизации запроса с помощью Cookie
 */
@Setter
@Log4j2
public class CookieAuthenticationFilter extends AbstractFilter {
  /**
   * Сервис для работы с безопасностью
   */
  private final SecurityService securityService;
  /**
   * Конвертер запроса в объект безопасности
   */
  private final AuthenticationConverter authenticationConverter;
  /**
   * Аутентификатор объекта безопасности
   */
  private final UserAuthenticator userAuthenticator;
  /**
   * Мапа урлов для игнорирования данным фильтром, ключ сам урл для игнорирования, значение это сет методов, который будет игнорироваться по данному урлу
   */
  private final Map<String, Set<String>> ignoredRequests = new HashMap<>();
  private final ObjectMapper objectMapper;

  public CookieAuthenticationFilter(ObjectMapper objectMapper, SecurityService securityService, AuthenticationConverter authenticationConverter, UserAuthenticator userAuthenticator) {
    this.securityService = securityService;
    this.authenticationConverter = authenticationConverter;
    this.userAuthenticator = userAuthenticator;
    this.objectMapper = objectMapper;
  }

  /**
   * @return
   */
  @Override
  protected ObjectMapper getObjectMapper() {
    return this.objectMapper;
  }

  /**
   * Фильтр для обработки аутентификации через Cookie.
   * Этот метод выполняет основную логику фильтрации запросов на основе Cookie.
   * Если запрос не игнорируется, он пытается получить аутентификационные данные из Cookie,
   * а затем производит аутентификацию пользователя и добавляет его в контекст безопасности,
   * прежде чем передать запрос по цепочке фильтров.
   * Если аутентификация не удалась из-за отсутствия Cookie или недействительного токена,
   * возвращается соответствующий статус ответа.
   *
   * @param req   объект HttpServletRequest, который содержит запрос, отправленный клиентом
   * @param res   объект HttpServletResponse, который содержит ответ, отправляемый фильтром клиенту
   * @param chain объект FilterChain для вызова следующего фильтра или ресурса
   * @throws IOException
   * @throws ServletException
   */
  @Override
  protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
    var requestURI = req.getRequestURI();
    var method = req.getMethod();
    var ignored
            = this.ignoredRequests.keySet().stream()
            .noneMatch(
                    uri -> {
                      if (uri.equals(requestURI)) {
                        return this.ignoredRequests.get(uri).stream().anyMatch(methodIgnored -> methodIgnored.equals(method));
                      }
                      return false;
                    });

    if (ignored) {
      try {
        var preAuthenticated = this.authenticationConverter.convert(req);
        if (preAuthenticated == null) {
          var errorMessage = "Запрос на аутентификацию не обработан, так как не удалось достать Cookie из запроса";
          setExceptionResponse(res, errorMessage, HttpServletResponse.SC_UNAUTHORIZED);
          return;
        }
        var authentication = this.userAuthenticator.authentication(preAuthenticated);
        if (authentication == null) {
          var errorMessage = "Запрос на аутентификацию не обработан, так как токен не действителен";
          setExceptionResponse(res, errorMessage, HttpServletResponse.SC_UNAUTHORIZED);
          return;
        }
        this.securityService.addPrincipal(authentication);
        super.doFilter(req, res, chain);
        return;
      } catch (BadCredentialsException e) {
        log.error("Ошибка авторизации с помощью Cookie. " + e.getMessage());
        setExceptionResponse(res, e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }
    }
    super.doFilter(req, res, chain);
  }

  /**
   * Добавляет URL для игнорирования фильтром безопасности.
   *
   * @param url         урл, который будет проигнорирован фильтром
   * @param httpMethods метод запроса, который будет проигнорирован
   */
  public void addIgnoredRequest(String url, String... httpMethods) {
    this.ignoredRequests.put(url, Arrays.stream(httpMethods).collect(Collectors.toSet()));
  }

}

package com.denknd.in.filters;

import com.denknd.exception.BadCredentialsException;
import com.denknd.security.entity.PreAuthenticatedAuthenticationToken;
import com.denknd.security.entity.UserSecurity;
import com.denknd.security.service.SecurityService;
import com.denknd.security.utils.authenticator.UserAuthenticator;
import com.denknd.security.utils.converter.AuthenticationConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Фильтр для авторизации запроса.
 */
@Setter
@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationFilter extends HttpFilter implements ExceptionResponse {
  /**
   * Сервис для работы с безопасностью.
   */
  private final SecurityService securityService;
  /**
   * Конвертеры аутентификации.
   */
  private final List<AuthenticationConverter> authenticationConverters;
  /**
   * Аутентификаторы пользователей.
   */
  private final List<UserAuthenticator> userAuthenticators;
  /**
   * Мапа URL-ов для игнорирования данным фильтром.
   * Ключ - URL для игнорирования, значение - множество методов, которые будут игнорироваться для данного URL-а.
   */
  private final Map<String, Set<String>> ignoredRequests = new HashMap<>();
  /**
   * Объект для преобразования объектов в JSON и обратно.
   */
  private final ObjectMapper objectMapper;

  /**
   * Добавляет URL для игнорирования фильтром безопасности.
   *
   * @param url         URL, который будет проигнорирован фильтром.
   * @param httpMethods Метод запроса, который будет проигнорирован.
   */
  public void addIgnoredRequest(String url, String... httpMethods) {
    this.ignoredRequests.put(url, Arrays.stream(httpMethods).collect(Collectors.toSet()));
  }

  /**
   * Фильтр для обработки аутентификации.
   * Если запрос не игнорируется, он пытается получить аутентификационные данные из запроса,
   * а затем производит аутентификацию пользователя и добавляет его в контекст безопасности,
   * прежде чем передать запрос по цепочке фильтров.
   * Если аутентификация не удалась из-за отсутствия данных для аутентификации или невалидных данных,
   * возвращается соответствующий статус ответа.
   *
   * @param req   Объект HttpServletRequest, который содержит запрос, отправленный клиентом.
   * @param res   Объект HttpServletResponse, который содержит ответ, отправляемый фильтром клиенту.
   * @param chain Объект FilterChain для вызова следующего фильтра или ресурса.
   * @throws IOException      Ошибка при обработке потока.
   * @throws ServletException Ошибка сервлета.
   */
  @Override
  public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
    var requestURI = req.getRequestURI();
    res.setCharacterEncoding("UTF-8");
    var method = req.getMethod();
    var ignored
            = this.isAnyMatch(method, requestURI);
    if (ignored) {
      try {
        var preAuthenticated = this.convertRequest(req);
        if (preAuthenticated == null) {
          var errorMessage = "Запрос на аутентификацию не обработан, не удалось получить данные о пользователе.";
          setExceptionResponse(res, errorMessage, HttpStatus.UNAUTHORIZED);
          return;
        }
        var authentication = this.authentication(preAuthenticated);
        if (authentication == null) {
          var errorMessage = "Запрос на аутентификацию не обработан, так как учетные данные не верные.";
          setExceptionResponse(res, errorMessage, HttpStatus.UNAUTHORIZED);
          return;
        }
        this.securityService.addPrincipal(authentication);
        log.info("Авторизация успешна. Идентификатор пользователя: " + authentication.userId());
        super.doFilter(req, res, chain);
        return;
      } catch (BadCredentialsException e) {
        log.error("Ошибка авторизации. " + e.getMessage());
        setExceptionResponse(res, e.getMessage(), HttpStatus.UNAUTHORIZED);
        return;
      }
    }
    super.doFilter(req, res, chain);
  }

  /**
   * Преобразует HTTP-запрос в объект аутентификации на основе доступных конвертеров аутентификации.
   *
   * @param req HTTP-запрос, который необходимо преобразовать в объект аутентификации
   * @return объект аутентификации или null, если не удалось выполнить преобразование
   * @throws BadCredentialsException если произошла ошибка при обработке недействительных учетных данных
   */
  private PreAuthenticatedAuthenticationToken convertRequest(HttpServletRequest req) throws BadCredentialsException {
    for (var authenticationConverter : this.authenticationConverters) {
      var preAuthenticatedAuthenticationToken = authenticationConverter.convert(req);
      if (preAuthenticatedAuthenticationToken != null) {
        return preAuthenticatedAuthenticationToken;
      }
    }
    return null;
  }

  /**
   * Проводит аутентификацию пользователя на основе доступных аутентификаторов пользователей.
   *
   * @param preAuthenticatedAuthenticationToken объект аутентификации, полученный из HTTP-запроса
   * @return объект безопасности пользователя или null, если аутентификация не удалась
   */
  private UserSecurity authentication(PreAuthenticatedAuthenticationToken preAuthenticatedAuthenticationToken) {
    for (var userAuthenticator : this.userAuthenticators) {
      var userSecurity = userAuthenticator.authentication(preAuthenticatedAuthenticationToken);
      if (userSecurity != null) {
        return userSecurity;
      }
    }
    return null;
  }

  /**
   * Проверяет, соответствует ли запрос игнорируемым URL-ам и методам.
   *
   * @param method HTTP-метод запроса
   * @param uri    URI запроса
   * @return true, если запрос соответствует игнорируемым URL-ам и методам, иначе false
   */
  private boolean isAnyMatch(String method, String uri) {
    var exactMatch = new HashSet<String>();
    var notExactMatch = new HashSet<String>();
    this.ignoredRequests.keySet().forEach(key -> {
      if (key.contains("*") || key.contains(".*")) {
        notExactMatch.add(key);
      } else {
        exactMatch.add(key);
      }
    });
    for (String pattern : exactMatch) {
      if (pattern.equals(uri)) {
        Set<String> ignoredMethods = this.ignoredRequests.get(pattern);
        if (ignoredMethods.contains(method)) {
          return false;
        }
      }
    }
    for (String pattern : notExactMatch) {
      var splitPattern = pattern.split("/");
      var splitUri = uri.split("/");
      boolean result = true;
      var size = Math.min(splitPattern.length - 1, splitUri.length);
      for (int i = 0; i < size; i++) {
        if (splitPattern[i].equals(splitUri[i])) {
          result = false;
        } else {
          return true;
        }
      }
      return result;
    }
    return true;
  }

  /**
   * Получает экземпляр ObjectMapper для преобразования объектов в JSON.
   *
   * @return Объект ObjectMapper.
   */
  @Override
  public ObjectMapper getObjectMapper() {
    return this.objectMapper;
  }

  /**
   * Получает экземпляр логгера для записи логов.
   *
   * @return Экземпляр логгера.
   */
  @Override
  public Logger logger() {
    return log;
  }

}

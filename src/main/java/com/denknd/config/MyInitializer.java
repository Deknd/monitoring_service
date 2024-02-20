package com.denknd.config;

import com.denknd.util.DataBaseConnection;
import com.nimbusds.jose.KeyLengthException;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Set;

/**
 * Инициализирует контекст приложения
 */
@Slf4j
@Setter
public class MyInitializer implements ServletContainerInitializer {
  /**
   * путь для изменения стандартного пути загрузки конфигурации, применяется для тестов
   */
  private String yamlPath = null;
  /**
   * Дополнительный конект к БД, применяется для тестов
   */
  private DataBaseConnection dataBaseConnection = null;
  /**
   * Метод вызывается контейнером сервлетов при запуске веб-приложения для инициализации.
   * @param c   Множество классов приложения, которые расширяют, реализуют или были аннотированы типами классов, указанными в аннотации
   * @param ctx Контекст сервлета веб-приложения, которое запускается
   */
  @Override
  public void onStartup(Set<Class<?>> c, ServletContext ctx){
    try {
      var manualConfig = new ManualConfig(yamlPath, dataBaseConnection);
      ctx.setAttribute("context", manualConfig);
      addBasicAuthenticationFilter(ctx, manualConfig);
      addCookieAuthenticationFilter(ctx, manualConfig);
      addLogoutFilter(ctx, manualConfig);

      log.info("Инициализация приложения прошла успешна");

    } catch (FileNotFoundException | ParseException | KeyLengthException e) {
      log.error("Ошибка инициализации контекста приложения. Приложения не может быть запущено.");
      throw new RuntimeException(e);
    }


  }

  /**
   * Инициализирует фильтр для блокировки токена доступа
   * @param ctx контекст приложения
   * @param manualConfig конфигурация контекста
   */
  private void addLogoutFilter(ServletContext ctx, ManualConfig manualConfig) {
    var logout = manualConfig.getLogoutFilter();
    var logoutFilter = ctx.addFilter(
            "LogoutFilter",
            logout
    );
    logoutFilter.addMappingForUrlPatterns(null, false, logout.getURL_PATTERNS());
  }

  /**
   * Инициализирует фильтр для аутентификации по куки, основной фильтр безопасности
   * @param ctx контекст приложения
   * @param manualConfig конфигурация контекста
   */
  private void addCookieAuthenticationFilter(ServletContext ctx, ManualConfig manualConfig) {
    var authenticationFilter = manualConfig.getCookieAuthenticationFilter();
    authenticationFilter.addIgnoredRequest(manualConfig.getBasicAuthenticationFilter().getURL_PATTERNS(), "POST");
    authenticationFilter.addIgnoredRequest("/users/signup","POST");
    var cookieAuthenticationFilter
            = ctx.addFilter(
            "CookieAuthenticationFilter",
            authenticationFilter);
    cookieAuthenticationFilter.addMappingForUrlPatterns(
            null, false, "/*"
    );
  }
  /**
   * Инициализирует фильтр для аутентификации по электронной почте и паролю, служит для получения токена доступа
   * @param ctx контекст приложения
   * @param manualConfig конфигурация контекста
   */
  private void addBasicAuthenticationFilter(ServletContext ctx, ManualConfig manualConfig) {
    var manualConfigBasicAuthenticationFilter = manualConfig.getBasicAuthenticationFilter();
    var basicAuthenticationFilter
            = ctx.addFilter(
            "BasicAuthenticationFilter",
            manualConfigBasicAuthenticationFilter);
    basicAuthenticationFilter.addMappingForUrlPatterns(
            null, true, manualConfigBasicAuthenticationFilter.getURL_PATTERNS());

  }
}

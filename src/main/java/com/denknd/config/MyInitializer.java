package com.denknd.config;

import com.denknd.in.filters.AuthenticationFilter;
import com.denknd.in.filters.BasicAuthenticationFilter;
import com.denknd.in.filters.LogoutFilter;
import com.denknd.util.impl.LiquibaseMigration;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Инициализирует контекст приложения
 */
@Slf4j
@Setter
public class MyInitializer implements WebApplicationInitializer {


  /**
   * Метод для инициализации приложения
   *
   * @param servletContext ошибка при инициализации сервлета
   */
  @Override
  public void onStartup(ServletContext servletContext) {
    servletContext.setRequestCharacterEncoding("UTF-8");
    servletContext.setResponseCharacterEncoding("UTF-8");
    var rootContext = new AnnotationConfigWebApplicationContext();
    rootContext.register(AppConfig.class, AspectConfig.class);
    servletContext.addListener(new ContextLoaderListener(rootContext));

    rootContext.refresh();
    var liquibaseMigration = rootContext.getBean(LiquibaseMigration.class);
    liquibaseMigration.migration();
    var dispatcherContext = new AnnotationConfigWebApplicationContext();
    dispatcherContext.register(WebConfig.class, SwaggerConfig.class);
    var dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(dispatcherContext));
    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping("/");
    addFilters(rootContext, servletContext);

  }

  /**
   * Добавление и настройка контекста фильтров
   *
   * @param rootContext    основной контекст приложения
   * @param servletContext контекст сервлетов
   */
  private void addFilters(AnnotationConfigWebApplicationContext rootContext, ServletContext servletContext) {
    var logoutFilter = rootContext.getBean(LogoutFilter.class);
    var logoutFilterRegistration
            = servletContext.addFilter("logoutFilter", logoutFilter);
    logoutFilterRegistration.addMappingForUrlPatterns(null, false, logoutFilter.getURL_PATTERNS());

    var basicAuthenticationFilter = rootContext.getBean(BasicAuthenticationFilter.class);
    var basicAuthenticationFilterRegistration
            = servletContext.addFilter("basicAuthenticationFilter", basicAuthenticationFilter);
    basicAuthenticationFilterRegistration.addMappingForUrlPatterns(null, false, basicAuthenticationFilter.getURL_PATTERNS());

    var cookieAuthenticationFilter
            = rootContext.getBean(AuthenticationFilter.class);
    cookieAuthenticationFilter.addIgnoredRequest("/auth/login", HttpMethod.POST.name());
    cookieAuthenticationFilter.addIgnoredRequest("/users", HttpMethod.POST.name());
    cookieAuthenticationFilter.addIgnoredRequest("/swagger/.*", HttpMethod.GET.name());
    cookieAuthenticationFilter.addIgnoredRequest("/swagger-ui/.*", HttpMethod.GET.name());
    cookieAuthenticationFilter.addIgnoredRequest("/v3/api-docs", HttpMethod.GET.name());
    cookieAuthenticationFilter.addIgnoredRequest(basicAuthenticationFilter.getURL_PATTERNS(), HttpMethod.POST.name());
    var cookieAuthenticationFilterRegistration
            = servletContext.addFilter("cookieAuthenticationFilter", cookieAuthenticationFilter);
    cookieAuthenticationFilterRegistration.addMappingForUrlPatterns(null, true, "/*");
  }


}


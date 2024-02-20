package com.denknd.config;

import com.denknd.repository.TestContainer;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyInitializerTest extends TestContainer {

  private MyInitializer myInitializer;

  @BeforeEach
  void setUp() {
    this.myInitializer = new MyInitializer();
    this.myInitializer.setYamlPath("src/test/resources/application-testConfig.yaml");
    this.myInitializer.setDataBaseConnection(postgresContainer.getDataBaseConnection());
  }

  @Test
  @DisplayName("Проверяет, что инициализируется контекст приложения, и добавляется 3 фильтра")
  void onStartup() {
    Set<Class<?>> setMock = mock(Set.class);
    var servletContext = mock(ServletContext.class);
    when(servletContext.addFilter(anyString(), any(Filter.class))).thenReturn(mock(FilterRegistration.Dynamic.class));

    assertThatCode(()-> this.myInitializer.onStartup(setMock, servletContext)).doesNotThrowAnyException();

    verify(servletContext, times(3)).addFilter(anyString(), any(Filter.class));
  }
}
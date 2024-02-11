package com.denknd.in.servlets;

import com.denknd.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AbstractServletTest {
  private AutoCloseable autoCloseable;
  @Mock
  private ObjectMapper objectMapper;
  private AbstractServlet abstractServlet;

  @BeforeEach
  void setUp() {
    this.autoCloseable = MockitoAnnotations.openMocks(this);

    this.abstractServlet = new AbstractServlet() {
      @Override
      protected ObjectMapper getObjectMapper() {
        return objectMapper;
      }
    };
  }

  @AfterEach
  void tearDown() throws Exception {
    this.autoCloseable.close();
  }

  @Test
  @DisplayName("Проверяет, что к ответу добавляется нужный статус и записывается объект")
  void responseCreate() throws IOException {
    var response = mock(HttpServletResponse.class);
    var printWriter = mock(PrintWriter.class);
    when(response.getWriter()).thenReturn(printWriter);
    var error = new ErrorResponse("test");

    this.abstractServlet.responseCreate(response, error, HttpServletResponse.SC_BAD_REQUEST);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(this.objectMapper, times(1)).writeValue(eq(printWriter), eq(error));

  }
}
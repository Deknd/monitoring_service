package com.denknd.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

/**
 * Выбрасывается, когда происходит ошибка подачей показаний.
 */
@RequiredArgsConstructor
@Getter
public class MeterReadingConflictError extends RuntimeException implements ErrorResponse {
  private final String message;

  /**
   * Получает HTTP статус код для ошибки.
   *
   * @return HTTP статус код
   */
  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.CONFLICT;
  }

  /**
   * Получает тело ответа для ошибки.
   *
   * @return тело ответа
   */
  @Override
  public ProblemDetail getBody() {
    return ProblemDetail.forStatusAndDetail(this.getStatusCode(), this.message);
  }
}

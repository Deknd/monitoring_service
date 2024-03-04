package com.denknd.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

@RequiredArgsConstructor
@Getter
public class AccessDeniedException extends RuntimeException implements ErrorResponse {
  private final String message;


  /**
   * Получает HTTP статус код для ошибки.
   *
   * @return HTTP статус код
   */
  @Override
  public HttpStatusCode getStatusCode() {
    return HttpStatus.BAD_REQUEST;
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

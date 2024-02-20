package com.denknd.in.controllers;

import com.denknd.exception.AddressDatabaseException;
import com.denknd.exception.InvalidUserDataException;
import com.denknd.exception.MeterReadingConflictError;
import com.denknd.exception.TypeMeterAdditionException;
import com.denknd.exception.UserAlreadyExistsException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Класс-контроллер для обработки ошибок в приложении.
 */
@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

  /**
   * Метод обрабатывает ошибку при валидации данных.
   *
   * @param ex      Исключение, которое нужно обработать.
   * @param headers Заголовки, которые будут отправлены в ответ.
   * @param status  Выбранный статус ответа.
   * @param request Текущий запрос.
   * @return Ответ с соответствующим статусом и телом.
   */
  @Override
  public ResponseEntity<Object> handleMethodArgumentNotValid(
          MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    var collect = Arrays.stream(ex.getDetailMessageArguments()).map(Object::toString).collect(Collectors.joining(""));
    var body = createProblemDetail(ex, HttpStatus.BAD_REQUEST, "Нет обязательных полей: " + collect, null, null, request);
    return handleExceptionInternal(ex, body, headers, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(AddressDatabaseException.class)
  public ResponseEntity<Object> handlerAddressDatabaseException(
          AddressDatabaseException ex, WebRequest request) {
    var headers = new HttpHeaders();
    return handleExceptionInternal(ex, null, headers, ex.getStatusCode(), request);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Object> handlerAccessDeniedException(
          AccessDeniedException ex, WebRequest request
  ) {
    var headers = new HttpHeaders();
    var body = createProblemDetail(ex, HttpStatus.FORBIDDEN, ex.getMessage(), null, null, request);

    return handleExceptionInternal(ex, body, headers, HttpStatus.FORBIDDEN, request);
  }

  @ExceptionHandler(SQLException.class)
  public ResponseEntity<Object> handlerSQLException(
          SQLException ex, WebRequest request
  ) {
    var headers = new HttpHeaders();
    var body = createProblemDetail(ex, HttpStatus.BAD_REQUEST, ex.getMessage(), null, null, request);
    return handleExceptionInternal(ex, body, headers, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(MeterReadingConflictError.class)
  public ResponseEntity<Object> handlerMeterReadingConflictError(
          MeterReadingConflictError ex, WebRequest request
  ) {
    var headers = new HttpHeaders();
    return handleExceptionInternal(ex, null, headers, ex.getStatusCode(), request);
  }

  @ExceptionHandler(TypeMeterAdditionException.class)
  public ResponseEntity<Object> handlerTypeMeterAdditionException(
          TypeMeterAdditionException ex, WebRequest request
  ) {
    var headers = new HttpHeaders();
    return handleExceptionInternal(ex, null, headers, ex.getStatusCode(), request);
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<Object> handlerUserAlreadyExistsException(
          UserAlreadyExistsException ex, WebRequest request) {
    var headers = new HttpHeaders();
    return handleExceptionInternal(ex, null, headers, ex.getStatusCode(), request);
  }

  @ExceptionHandler(NoSuchAlgorithmException.class)
  public ResponseEntity<Object> handlerNoSuchAlgorithmException(
          NoSuchAlgorithmException ex, WebRequest request
  ) {
    var body = createProblemDetail(ex, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null, null, request);
    var headers = new HttpHeaders();
    return handleExceptionInternal(ex, body, headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
  }
  @ExceptionHandler(InvalidUserDataException.class)
  public ResponseEntity<Object> handlerInvalidUserDataException(
          InvalidUserDataException ex, WebRequest request
  ){
    var headers = new HttpHeaders();
    return handleExceptionInternal(ex, null, headers, ex.getStatusCode(), request);
  }
}

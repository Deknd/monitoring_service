package com.denknd.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Класс для передачи ошибки пользователю
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
  private String message;
}

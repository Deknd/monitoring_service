package com.denknd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Класс для хранения данных о пользователи.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
  /**
   * Идентификатор.
   */
  private Long userId;
  /**
   * Имя.
   */
  private String firstName;
  /**
   * Фамилия.
   */
  private String lastName;
  /**
   * емайл.
   */
  private String email;
  /**
   * пароль.
   */
  private String password;
  /**
   * адреса.
   */
  private List<Address> addresses;
  /**
   * роль.
   */
  private Roles role;
}

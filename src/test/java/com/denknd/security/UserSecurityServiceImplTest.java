package com.denknd.security;

import com.denknd.entity.User;
import com.denknd.mappers.UserMapper;
import com.denknd.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserSecurityServiceImplTest {
  @Mock
  private UserService userService;
  @Mock
  private UserMapper userMapper;

  private UserSecurityService userSecurityService;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.userSecurityService = new UserSecurityServiceImpl(this.userService, this.userMapper);
  }

  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }

  @Test
  @DisplayName("Проверяет, что метод обращается во все сервисы и возвращает объект UserSecurity")
  void getUserSecurity() {
    var email = "email";
    when(this.userService.existUserByEmail(eq(email))).thenReturn(true);
    when(this.userService.getUserByEmail(eq(email))).thenReturn(mock(User.class));

    this.userSecurityService.getUserSecurity(email);

    verify(this.userService, times(1)).getUserByEmail(eq(email));
    verify(this.userMapper, times(1)).mapUserToUserSecurity(any());
  }

  @Test
  @DisplayName("Проверяет, что метод не обращается в сервисы и возвращает объект null")
  void getUserSecurity_notUser() {
    var email = "email";
    when(this.userService.existUserByEmail(eq(email))).thenReturn(false);

    var userSecurity = this.userSecurityService.getUserSecurity(email);

    verify(this.userService, times(0)).getUserByEmail(eq(email));
    assertThat(userSecurity).isNull();
  }
}
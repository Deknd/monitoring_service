package com.denknd.security;

import com.denknd.util.PasswordEncoder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SecurityServiceImplTest {
  @Mock
  private UserSecurityService userSecurityService;
  @Mock
  private PasswordEncoder passwordEncoder;
  private SecurityServiceImpl securityService;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.securityService = new SecurityServiceImpl(this.userSecurityService, this.passwordEncoder);
  }
  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }
  @Test
  @DisplayName("Проверяет, что при успешной аутентификации, возвращается юзер")
  void authentication() {
    var email = "email";
    var password = "password";
    var userSecurity = mock(UserSecurity.class);
    when(this.userSecurityService.getUserSecurity(eq(email))).thenReturn(userSecurity);
    when(this.passwordEncoder.matches(eq(password), any())).thenReturn(true);

    var authentication = this.securityService.authentication(email, password);

    assertThat(authentication).isEqualTo(userSecurity);
    verify(this.userSecurityService, times(1)).getUserSecurity(eq(email));
  }

  @Test
  @DisplayName("Проверяет, что при не успешной проверки пароля, возвращает null")
  void authentication_failedPassword() {
    var email = "email";
    var password = "password";
    var userSecurity = mock(UserSecurity.class);
    when(this.userSecurityService.getUserSecurity(eq(email))).thenReturn(userSecurity);
    when(this.passwordEncoder.matches(eq(password), any())).thenReturn(false);

    var authentication = this.securityService.authentication(email, password);

    assertThat(authentication).isNull();
    verify(this.userSecurityService, times(1)).getUserSecurity(eq(email));
  }

  @Test
  @DisplayName("Проверяет, что если пользователя не существует под данным эмейл, возвращает null")
  void authentication_notUser() {
    var email = "email";
    var password = "password";
    when(this.userSecurityService.getUserSecurity(eq(email))).thenReturn(null);

    var authentication = this.securityService.authentication(email, password);

    assertThat(authentication).isNull();
    verify(this.userSecurityService, times(1)).getUserSecurity(eq(email));
  }

  @Test
  @DisplayName("Проверяет, что метод возвращает авторизованного пользователя")
  void getUserSecurity() {
    var userSecurity = mock(UserSecurity.class);
    when(this.userSecurityService.getUserSecurity(any())).thenReturn(userSecurity);
    when(this.passwordEncoder.matches(any(), any())).thenReturn(true);
    this.securityService.authentication("email", "password");

    var security = this.securityService.getUserSecurity();

    assertThat(security).isEqualTo(userSecurity);
  }
  @Test
  @DisplayName("Проверяет, что метод возвращает авторизованного пользователя")
  void getUserSecurity_notAuthentication() {

    var security = this.securityService.getUserSecurity();

    assertThat(security).isNull();
  }

  @Test
  @DisplayName("Проверяет, что если пользователь авторизован, будет возвращать true")
  void isAuthentication() {
    var userSecurity = mock(UserSecurity.class);
    when(this.userSecurityService.getUserSecurity(any())).thenReturn(userSecurity);
    when(this.passwordEncoder.matches(any(), any())).thenReturn(true);
    this.securityService.authentication("email", "password");

    var security = this.securityService.isAuthentication();

    assertThat(security).isEqualTo(true);
  }

  @Test
  @DisplayName("Проверяет, что если пользователь не авторизован, будет возвращать false")
  void isAuthentication_notAuthentication() {
    var security = this.securityService.isAuthentication();

    assertThat(security).isEqualTo(false);
  }

  @Test
  void logout(){
    var userSecurity = mock(UserSecurity.class);
    when(this.userSecurityService.getUserSecurity(any())).thenReturn(userSecurity);
    when(this.passwordEncoder.matches(any(), any())).thenReturn(true);
    this.securityService.authentication("email", "password");

    var logout = this.securityService.logout();

    assertThat(logout).isTrue();
    assertThat(this.securityService.isAuthentication()).isFalse();
  }
}
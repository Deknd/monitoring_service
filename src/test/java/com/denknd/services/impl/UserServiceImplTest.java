package com.denknd.services.impl;

import com.denknd.entity.Parameters;
import com.denknd.entity.Roles;
import com.denknd.entity.User;
import com.denknd.exception.AccessDeniedException;
import com.denknd.exception.InvalidUserDataException;
import com.denknd.exception.UserAlreadyExistsException;
import com.denknd.repository.UserRepository;
import com.denknd.security.entity.UserSecurity;
import com.denknd.security.service.SecurityService;
import com.denknd.util.PasswordEncoder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {
  @Mock
  private UserRepository userRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private SecurityService securityService;
  private UserServiceImpl userServiceImpl;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.userServiceImpl = new UserServiceImpl(this.userRepository, this.passwordEncoder, this.securityService);
  }

  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }

  @Test
  @DisplayName("Проверяет, что пароль кодируется и вызывается метод репозитория save")
  void registrationUser() throws UserAlreadyExistsException, InvalidUserDataException, NoSuchAlgorithmException, SQLException, AccessDeniedException {

    var testPassword = "testPassword";
    var user = User.builder()
            .email("test@email.com")
            .password(testPassword)
            .build();
    when(this.securityService.isAuthentication()).thenReturn(false);

    this.userServiceImpl.registrationUser(user);

    verify(this.userRepository, times(1)).save(any(User.class));
    verify(this.passwordEncoder, times(1)).encode(eq(testPassword));
  }

  @Test
  @DisplayName("Проверяет, что пароль кодируется и вызывается метод репозитория save, а репозиторий выкидывает ошибку")
  void registrationUser_repositoryException() throws NoSuchAlgorithmException, SQLException {

    var testPassword = "testPassword";
    var user = User.builder()
            .email("test@email.com")
            .password(testPassword)
            .build();
    when(this.userRepository.save(any(User.class))).thenThrow(SQLException.class);
    when(this.securityService.isAuthentication()).thenReturn(false);

    assertThatThrownBy(() -> this.userServiceImpl.registrationUser(user));

    verify(this.userRepository, times(1)).save(any(User.class));
    verify(this.passwordEncoder, times(1)).encode(eq(testPassword));
  }

  @Test
  @DisplayName("Проверяет, то выкидывается исключение, если пользователь существует")
  void registrationUser_throwException() throws SQLException, NoSuchAlgorithmException {

    var testPassword = "testPassword";
    var user = User.builder()
            .email("test@email.com")
            .password(testPassword)
            .build();
    when(this.userRepository.existUser(eq(user.getEmail()))).thenReturn(true);
    when(this.securityService.isAuthentication()).thenReturn(false);

    assertThatThrownBy(() -> this.userServiceImpl.registrationUser(user)).isInstanceOf(UserAlreadyExistsException.class);

    verify(this.userRepository, times(0)).save(any(User.class));
    verify(this.passwordEncoder, times(0)).encode(eq(testPassword));
  }

  @Test
  @DisplayName("Проверяет, что выкидывается исключение, если пользователь авторизирован")
  void registrationUser_throwAccessDeniedException() throws SQLException, NoSuchAlgorithmException {

    var testPassword = "testPassword";
    var user = User.builder()
            .email("test@email.com")
            .password(testPassword)
            .build();
    when(this.securityService.isAuthentication()).thenReturn(true);

    assertThatThrownBy(() -> this.userServiceImpl.registrationUser(user)).isInstanceOf(AccessDeniedException.class);

    verify(this.userRepository, times(0)).save(any(User.class));
    verify(this.passwordEncoder, times(0)).encode(eq(testPassword));
  }


  @Test
  @DisplayName("Проверяет вызов репозитория")
  void existUser() {
    this.userServiceImpl.existUser(1L);

    verify(this.userRepository, times(1)).existUserByUserId(eq(1L));
  }

  @Test
  @DisplayName("Проверяет что вызывается репозиторий, когда вызывается пользователем с ролью юзер")
  void getUserById() {
    var userId = 1L;
    var email = "email";
    var parameters = Parameters.builder()
            .userId(userId).email(email).build();
    var userSecurity = UserSecurity.builder().role(Roles.USER).userId(3L).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);
    when(this.userRepository.findById(eq(userSecurity.userId())))
            .thenReturn(Optional.of(mock(User.class)));

    this.userServiceImpl.getUser(parameters);

    verify(this.userRepository, times(1))
            .findById(eq(userSecurity.userId()));
    verify(this.userRepository, times(0))
            .find(any());
  }
  @Test
  @DisplayName("Проверяет что вызывается репозиторий, когда вызывается пользователем с ролью юзер")
  void getUserById_admin() {
    var userId = 1L;
    var email = "email";
    var parameters = Parameters.builder()
            .userId(userId).email(email).build();
    var userSecurity = UserSecurity.builder().role(Roles.ADMIN).userId(3L).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);
    when(this.userRepository.findById(eq(userSecurity.userId())))
            .thenReturn(Optional.of(mock(User.class)));

    this.userServiceImpl.getUser(parameters);

    verify(this.userRepository, times(1))
            .findById(eq(userId));
    verify(this.userRepository, times(0))
            .find(any());
  }
  @Test
  @DisplayName("Проверяет что вызывается репозиторий, когда вызывается пользователем с ролью юзер")
  void getUserById_admin_email() {
    var email = "email";
    var parameters = Parameters.builder()
            .email(email).build();
    var userSecurity = UserSecurity.builder().role(Roles.ADMIN).userId(3L).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);
    when(this.userRepository.findById(eq(userSecurity.userId())))
            .thenReturn(Optional.of(mock(User.class)));

    this.userServiceImpl.getUser(parameters);

    verify(this.userRepository, times(1))
            .find(eq(email));
    verify(this.userRepository, times(0))
            .findById(any());
  }
  @Test
  @DisplayName("Проверяет что вызывается репозиторий, когда вызывается пользователем с ролью юзер")
  void getUserById_admin_nullParam() {
    var parameters = Parameters.builder()
            .build();
    var userSecurity = UserSecurity.builder().role(Roles.ADMIN).userId(3L).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);
    when(this.userRepository.findById(eq(userSecurity.userId())))
            .thenReturn(Optional.of(mock(User.class)));

    this.userServiceImpl.getUser(parameters);

    verify(this.userRepository, times(0))
            .find(any());
    verify(this.userRepository, times(0))
            .findById(any());
  }

  @Test
  @DisplayName("Проверяет вызов репозитория")
  void getUserByEmail() {
    var email = "email";
    this.userServiceImpl.getUserByEmail(email);

    verify(this.userRepository, times(1)).find(eq(email));
  }

  @Test
  @DisplayName("Проверяет вызов репозитория existUserByEmail")
  void existUserByEmail() {
    var email = "email";
    this.userServiceImpl.existUserByEmail(email);

    verify(this.userRepository, times(1)).existUser(eq(email));
  }

}
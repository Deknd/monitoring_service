package com.denknd.services.impl;

import com.denknd.entity.User;
import com.denknd.exception.InvalidUserDataException;
import com.denknd.exception.UserAlreadyExistsException;
import com.denknd.util.PasswordEncoder;
import com.denknd.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    private UserServiceImpl userServiceImpl;
    private AutoCloseable closeable;
    @BeforeEach
    void setUp() {
        this.closeable = MockitoAnnotations.openMocks(this);
        this.userServiceImpl = new UserServiceImpl(this.userRepository, this.passwordEncoder);
    }
    @AfterEach
    void tearDown() throws Exception {
        this.closeable.close();
    }
    @Test
    @DisplayName("Проверяет, что пароль кодируется и вызывается метод репозитория save")
    void registrationUser() throws UserAlreadyExistsException, InvalidUserDataException, NoSuchAlgorithmException, SQLException {

        var testPassword = "testPassword";
        var user = User.builder()
                .email("test@email.com")
                .password(testPassword)
                .build();

        this.userServiceImpl.registrationUser(user);

        verify(this.userRepository, times(1)).save(any(User.class));
        verify(this.passwordEncoder, times(1)).encode(eq(testPassword));
    }
    @Test
    @DisplayName("Проверяет, что пароль кодируется и вызывается метод репозитория save, а репозиторий выкидывает ошибку")
    void registrationUser_repositoryException() throws  NoSuchAlgorithmException, SQLException {

        var testPassword = "testPassword";
        var user = User.builder()
                .email("test@email.com")
                .password(testPassword)
                .build();
        when(this.userRepository.save(any(User.class))).thenThrow(SQLException.class);

        assertThatThrownBy(()-> this.userServiceImpl.registrationUser(user));

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

        assertThatThrownBy(()->this.userServiceImpl.registrationUser(user)).isInstanceOf(UserAlreadyExistsException.class);

        verify(this.userRepository, times(0)).save(any(User.class));
        verify(this.passwordEncoder, times(0)).encode(eq(testPassword));
    }



    @Test
    @DisplayName("Проверяет вызов репозитория")
    void existUser(){
        this.userServiceImpl.existUser(1L);

        verify(this.userRepository, times(1)).existUserByUserId(eq(1L));
    }
    @Test
    @DisplayName("Проверяет вызов репозитория")
    void getUserById(){
        var userId = 1L;
        this.userServiceImpl.getUserById(userId);

        verify(this.userRepository, times(1)).findById(eq(userId));
    }

    @Test
    @DisplayName("Проверяет вызов репозитория")
    void getUserByEmail(){
        var email = "email";
        this.userServiceImpl.getUserByEmail(email);

        verify(this.userRepository, times(1)).find(eq(email));
    }
    @Test
    @DisplayName("Проверяет вызов репозитория existUserByEmail")
    void existUserByEmail(){
        var email = "email";
        this.userServiceImpl.existUserByEmail(email);

        verify(this.userRepository, times(1)).existUser(eq(email));
    }

}
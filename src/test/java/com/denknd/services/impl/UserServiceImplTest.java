package com.denknd.services.impl;

import com.denknd.entity.User;
import com.denknd.exception.UserAlreadyExistsException;
import com.denknd.port.PasswordEncoder;
import com.denknd.port.UserRepository;
import com.denknd.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserServiceImpl userServiceImpl;
    @BeforeEach
    void setUp() {
        this.userRepository = mock(UserRepository.class);
        this.passwordEncoder = mock(PasswordEncoder.class);
        this.userServiceImpl = new UserServiceImpl(this.userRepository, this.passwordEncoder);
    }

    @Test
    @DisplayName("Проверяет, что пароль кодируется и вызывается метод репозитория save")
    void registrationUser() throws UserAlreadyExistsException {

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
    @DisplayName("Проверяет, то выкидывается исключение, если пользователь существует")
    void registrationUser_throwException() throws UserAlreadyExistsException {

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
    @DisplayName("Проверяет, что метод обращается в репозиторий и обращается к passwordEncoder ")
    void loginUser(){
        var email = "email";
        var password = "password";
        var mockUser = mock(User.class);
        when(this.userRepository.find(eq(email))).thenReturn(Optional.of(mockUser));
        when(this.passwordEncoder.matches(eq(password), any())).thenReturn(true);
        var result = this.userServiceImpl.loginUser(email, password);

        verify(this.userRepository, times(1)).find(eq(email));
        verify(this.passwordEncoder, times(1)).matches(eq(password), any());
        assertThat(result).isEqualTo(mockUser);
    }
    @Test
    @DisplayName("Проверяет, что метод обращается в репозиторий и обращается к passwordEncoder, ошибочный пароль возвращает null ")
    void loginUser_failedPassword(){
        var email = "email";
        var password = "password";
        var mockUser = mock(User.class);
        when(this.userRepository.find(eq(email))).thenReturn(Optional.of(mockUser));
        when(this.passwordEncoder.matches(eq(password), any())).thenReturn(false);

        var result = this.userServiceImpl.loginUser(email, password);

        verify(this.userRepository, times(1)).find(eq(email));
        verify(this.passwordEncoder, times(1)).matches(eq(password), any());
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Проверяет, что метод обращается в репозиторий, когда из репозитория приходит пустой Optional возвращает null ")
    void loginUser_notUser(){
        var email = "email";
        var password = "password";
        when(this.userRepository.find(eq(email))).thenReturn(Optional.empty());

        var result = this.userServiceImpl.loginUser(email, password);

        verify(this.userRepository, times(1)).find(eq(email));
        verify(this.passwordEncoder, times(0)).matches(any(), any());
        assertThat(result).isNull();
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
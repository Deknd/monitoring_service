package com.denknd.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordValidatorTest {

    private PasswordValidator passwordValidator;

    @BeforeEach
    void setUp() {
        this.passwordValidator = new PasswordValidator();
    }

    @Test
    @DisplayName("Проверяет тип валидации")
    void nameValidator() {
        var name = IValidator.PASSWORD_TYPE;

        var nameValidator = this.passwordValidator.nameValidator();

        assertThat(nameValidator).isEqualTo(name);
    }



    @Test
    @DisplayName("Проверяет, что валидный пароль возвращает true")
    void isValidation() {
        var password = "password";

        var validPassword = this.passwordValidator.isValidation(password);

        assertThat(validPassword).isTrue();
    }

    @Test
    @DisplayName("Проверяет, что не валидный пароль возвращает false")
    void isValidation_noValid() {
        var password = "pa";

        var noValidPassword = this.passwordValidator.isValidation(password);

        assertThat(noValidPassword).isFalse();
    }

    @Test
    @DisplayName("Проверяет, что пароль равный null возвращает false")
    void isValidation_null() {

        var noValidPassword = this.passwordValidator.isValidation(null);

        assertThat(noValidPassword).isFalse();
    }
}
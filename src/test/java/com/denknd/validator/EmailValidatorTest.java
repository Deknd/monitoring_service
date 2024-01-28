package com.denknd.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailValidatorTest {

    private EmailValidator emailValidator;

    @BeforeEach
    void setUp() {
        this.emailValidator = new EmailValidator();
    }

    @Test
    @DisplayName("Проверяет тип валидации")
    void nameValidator() {
        var name = IValidator.EMAIL_TYPE;

        var nameValidator = this.emailValidator.nameValidator();

        assertThat(nameValidator).isEqualTo(name);
    }

    @Test
    @DisplayName("Проверят, что валидный email возвращает true")
    void isValidation() {
        var email = "test@email.cm";

        var validEmail = this.emailValidator.isValidation(email);

        assertThat(validEmail).isTrue();
    }

    @Test
    @DisplayName("Проверят, что не валидный email возвращает false")
    void isValidation_noValid() {
        var email = "testemail.cm";

        var noValidEmail = this.emailValidator.isValidation(email);

        assertThat(noValidEmail).isFalse();
    }

    @Test
    @DisplayName("Проверят, что email = null возвращает false")
    void isValidation_null() {

        var noValidEmail = this.emailValidator.isValidation(null);

        assertThat(noValidEmail).isFalse();
    }
}
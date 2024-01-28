package com.denknd.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class ValidatorsImplTest {

    private ValidatorsImpl validators;
    private Scanner scanner;

    @BeforeEach
    void setUp() {
        this.validators = new ValidatorsImpl();
        this.scanner = mock(Scanner.class);
    }



    @Test
    @DisplayName("Проверяет, что при валидных вводимых данных срабатывает один раз")
    void isValid() {
        var dataPrint = "dataPrint";
        var nameValidator = "validator";
        var exception = "exception";
        var emailValidator = mock(IValidator.class);
        when(emailValidator.nameValidator()).thenReturn(nameValidator);
        this.validators.addValidator(emailValidator);
        when(emailValidator.isValidation(any())).thenReturn(true);

        this.validators.isValid(dataPrint, nameValidator, exception, scanner);

        verify(this.scanner, times(1)).nextLine();
        verify(emailValidator, times(1)).isValidation(any());
    }

    @Test
    @DisplayName("Проверяет, что при не валидных вводимых данных срабатывает три раз")
    void isValid_notValid() {
        var dataPrint = "dataPrint";
        var nameValidator = "validator";
        var exception = "exception";
        var emailValidator = mock(IValidator.class);
        when(emailValidator.nameValidator()).thenReturn(nameValidator);
        this.validators.addValidator(emailValidator);
        when(emailValidator.isValidation(any())).thenReturn(false);

        this.validators.isValid(dataPrint, nameValidator, exception, scanner);

        verify(this.scanner, times(3)).nextLine();
        verify(emailValidator, times(3)).isValidation(any());
    }

    @Test
    @DisplayName("Проверяет, что вводимые данные не являются null и пустыми")
    void notNullValue() {
        var result = this.validators.notNullValue("sad", "ASdas", " sadasf saf");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Проверяет, что вводимые пустые данные возвращают false")
    void notNullValue_false() {
        var result = this.validators.notNullValue("", "ASdas", " sadasf saf");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Проверяет, что вводимые null возвращают false")
    void notNullValue_falseNull() {
        var result = this.validators.notNullValue(null, "ASdas", " sadasf saf");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Проверяет, что добавляются валидаторы с одним именем")
    void addValidator(){
        var name = "test";
        var iValidator = mock(IValidator.class);
        var iValidator2 = mock(IValidator.class);
        when(iValidator.nameValidator()).thenReturn(name);
        when(iValidator2.nameValidator()).thenReturn(name);


        this.validators.addValidator(iValidator, iValidator2);

    }
    @Test
    @DisplayName("Проверяет, что добавляются валидаторы с одним именем")
    void addValidator_differentName(){
        var name = "test";
        var name2 = "test2";
        var iValidator = mock(IValidator.class);
        var iValidator2 = mock(IValidator.class);
        when(iValidator.nameValidator()).thenReturn(name);
        when(iValidator2.nameValidator()).thenReturn(name2);


        this.validators.addValidator(iValidator, iValidator2);

    }
}
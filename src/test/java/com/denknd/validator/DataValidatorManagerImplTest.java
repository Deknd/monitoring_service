package com.denknd.validator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class DataValidatorManagerImplTest {

    private DataValidatorManagerImpl validators;
    @Mock
    private Scanner scanner;
    private AutoCloseable closeable;
    @BeforeEach
    void setUp() {
        this.closeable = MockitoAnnotations.openMocks(this);
        this.validators = new DataValidatorManagerImpl(this.scanner);
    }

    @AfterEach
    void tearDown() throws Exception {
        this.closeable.close();
    }

    @Test
    @DisplayName("Проверяет, что при валидных вводимых данных срабатывает один раз")
    void isValid() {
        var dataPrint = "dataPrint";
        var nameValidator = "validator";
        var exception = "exception";
        var emailValidator = mock(Validator.class);
        when(emailValidator.nameValidator()).thenReturn(nameValidator);
        this.validators.addValidators(emailValidator);
        when(emailValidator.isValid(any())).thenReturn(true);

        this.validators.getValidInput(dataPrint, nameValidator, exception);

        verify(this.scanner, times(1)).nextLine();
        verify(emailValidator, times(1)).isValid(any());
    }

    @Test
    @DisplayName("Проверяет, что при не валидных вводимых данных срабатывает три раз")
    void isValid_notValid() {
        var dataPrint = "dataPrint";
        var nameValidator = "validator";
        var exception = "exception";
        var emailValidator = mock(Validator.class);
        when(emailValidator.nameValidator()).thenReturn(nameValidator);
        this.validators.addValidators(emailValidator);
        when(emailValidator.isValid(any())).thenReturn(false);

        this.validators.getValidInput(dataPrint, nameValidator, exception);

        verify(this.scanner, times(3)).nextLine();
        verify(emailValidator, times(3)).isValid(any());
    }

    @Test
    @DisplayName("Проверяет, что вводимые данные не являются null и пустыми")
    void notNullValue() {
        var result = this.validators.areAllValuesNotNullAndNotEmpty("sad", "ASdas", " sadasf saf");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Проверяет, что вводимые пустые данные возвращают false")
    void notNullValue_false() {
        var result = this.validators.areAllValuesNotNullAndNotEmpty("", "ASdas", " sadasf saf");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Проверяет, что вводимые null возвращают false")
    void notNullValue_falseNull() {
        var result = this.validators.areAllValuesNotNullAndNotEmpty(null, "ASdas", " sadasf saf");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Проверяет, что добавляются валидаторы с одним именем")
    void addValidator(){
        var name = "test";
        var iValidator = mock(Validator.class);
        var iValidator2 = mock(Validator.class);
        when(iValidator.nameValidator()).thenReturn(name);
        when(iValidator2.nameValidator()).thenReturn(name);


        this.validators.addValidators(iValidator, iValidator2);

    }
    @Test
    @DisplayName("Проверяет, что добавляются валидаторы с одним именем")
    void addValidator_differentName(){
        var name = "test";
        var name2 = "test2";
        var iValidator = mock(Validator.class);
        var iValidator2 = mock(Validator.class);
        when(iValidator.nameValidator()).thenReturn(name);
        when(iValidator2.nameValidator()).thenReturn(name2);


        this.validators.addValidators(iValidator, iValidator2);

    }
}
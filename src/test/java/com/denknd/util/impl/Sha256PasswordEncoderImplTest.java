package com.denknd.util.impl;

import com.denknd.util.impl.Sha256PasswordEncoderImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class Sha256PasswordEncoderImplTest {

    private Sha256PasswordEncoderImpl encode;

    @BeforeEach
    void setUp() {
        this.encode = new Sha256PasswordEncoderImpl();
    }

    @Test
    @DisplayName("Проверяет, что пароль закодировался")
    void encode() {
        var rawPassword = "12345";

//        var encodePassword = this.encode.encode(rawPassword);
//
//        System.out.println(encodePassword);
//        assertThat(encodePassword).isNotEqualTo(rawPassword);
    }

    @Test
    @DisplayName("Если передать null, выкинет ошибку")
    void encode_null(){
        String rawPassword = null;

        assertThatThrownBy(()-> this.encode.encode(rawPassword)).isInstanceOf(NullPointerException.class);
    }


    @Test
    @DisplayName("Проверяет, что пароли совпадают")
    void matches() {
        var encodePassword = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";
        var rawPassword = "password";

        var result = this.encode.matches(rawPassword, encodePassword);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Проверяет, что пароли не совпадают")
    void matches_false() {
        var encodePassword = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";
        var rawPassword = "password_false";

        var result = this.encode.matches(rawPassword, encodePassword);

        assertThat(result).isFalse();
    }
    @Test
    @DisplayName("Если передать null в качестве сырого пароля, выкинет ошибку")
    void matches_null() {
        var encodePassword = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";
        String rawPassword = null;
        assertThatThrownBy(()->  this.encode.matches(rawPassword, encodePassword)).isInstanceOf(NullPointerException.class);
    }
}
package com.denknd.adapter.repository;

import com.denknd.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryUserRepositoryTest {

    private InMemoryUserRepository repository;
    private User testUser;

    @BeforeEach
    void setUp() {
        var testUser = User.builder()
                .email("test@email.com")
                .build();
        this.repository = new InMemoryUserRepository();
        this.testUser = repository.save(testUser);
    }

    @Test
    @DisplayName("Проверяет, что пользователь существует")
    void existUser() {
        var email = "test@email.com";

        var existUser = this.repository.existUser(email);

        assertThat(existUser).isTrue();
    }

    @Test
    @DisplayName("Проверяет, что пользователь не существует")
    void existUser_noUser() {
        var email = "false@email.com";

        var existUser = this.repository.existUser(email);

        assertThat(existUser).isFalse();
    }

    @Test
    @DisplayName("Сохраняет нового пользователя в репозитории")
    void save() {
        var userTest = User.builder().email("Test@Email.com").build();

        var saveUser = this.repository.save(userTest);

        assertThat(saveUser.getUserId()).isNotNull();
        assertThat(this.repository.existUser(saveUser.getEmail())).isTrue();
    }

    @Test
    @DisplayName("Проверяет, что пользователь достается из памяти")
    void find(){
        var email = "test@email.com";

        var user = this.repository.find(email);

        assertThat(user).isPresent();
    }
    @Test
    @DisplayName("Проверяет, что пользователь достается из памяти")
    void find_notUser(){
        var email = "test";

        var user = this.repository.find(email);

        assertThat(user).isEmpty();
    }

    @Test
    @DisplayName("проверяет что пользователь с данным айди существует")
    void existUserByUserId(){
        var userId = this.testUser.getUserId();

        var result = this.repository.existUserByUserId(userId);

        assertThat(result).isTrue();
    }
    @Test
    @DisplayName("проверяет что пользователь с данным айди не существует")
    void existUserByUserId_notUser(){
        var userId = 12321543L;

        var result = this.repository.existUserByUserId(userId);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("проверяет, что по айди достается пользователь")
    void findById(){
        var userId = this.testUser.getUserId();

        var result = this.repository.findById(userId);

        assertThat(result).isPresent();
    }

    @Test
    @DisplayName("проверяет, что по не верному айди достается пустой опшинал")
    void findById_noUser(){
        var userId = 123L;

        var result = this.repository.findById(userId);

        assertThat(result).isEmpty();
    }
}
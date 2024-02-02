package com.denknd.repository.impl;

import com.denknd.entity.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryRoleRepositoryTest {

    private InMemoryRoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        this.roleRepository = new InMemoryRoleRepository();
        var role = Roles.USER;
        this.roleRepository.save(1L, role);
    }

    @Test
    @DisplayName("проверяет, что пользователю добавляется роль")
    void save() {
        var userId = 6L;
        var roleUser = Roles.USER;

        var save = this.roleRepository.save(userId, roleUser);

        assertThat(save).isTrue();
        var roles = this.roleRepository.findRolesByUserId(userId);
        assertThat(roles).isPresent();
        assertThat(roles.get()).isEqualTo(roleUser);
    }



    @Test
    @DisplayName("Проверяет, что достаются из памяти роли")
    void findRolesByUserId() {
        var userId = 1L;

        var roles = this.roleRepository.findRolesByUserId(userId);

        assertThat(roles).isPresent();
        assertThat(roles.get()).isEqualTo(Roles.USER);
    }

    @Test
    @DisplayName("Проверяет, что не достаются из памяти роли")
    void findRolesByUserId_isNotRole() {
        var userId = 5L;

        var roles = this.roleRepository.findRolesByUserId(userId);

        assertThat(roles).isEmpty();
    }
}
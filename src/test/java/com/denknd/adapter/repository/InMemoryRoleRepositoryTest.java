package com.denknd.adapter.repository;

import com.denknd.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryRoleRepositoryTest {

    private InMemoryRoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        this.roleRepository = new InMemoryRoleRepository();
        var role = Role.builder().roleName("USER").build();
        this.roleRepository.save(1L, role);
    }

    @Test
    @DisplayName("проверяет, что пользователю добавляется роль")
    void save() {
        var userId = 1L;
        var roleUser = Role.builder().roleName("USER").build();
        var roleAdmin = Role.builder().roleName("ADMIN").build();

        var save = this.roleRepository.save(userId, roleUser, roleAdmin);

        assertThat(save).isTrue();
        var roles = this.roleRepository.findRolesByUserId(userId);
        assertThat(roles.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("проверяет, что пользователю добавляется роль")
    void save_newUser() {
        var userId = 2L;
        var roleUser = Role.builder().roleName("USER").build();
        var roleAdmin = Role.builder().roleName("ADMIN").build();

        var save = this.roleRepository.save(userId, roleUser, roleAdmin);

        assertThat(save).isTrue();
        var roles = this.roleRepository.findRolesByUserId(userId);
        assertThat(roles.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Проверяет, что достаются из памяти роли")
    void findRolesByUserId() {
        var userId = 1L;

        var roles = this.roleRepository.findRolesByUserId(userId);

        assertThat(roles.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Проверяет, что не достаются из памяти роли")
    void findRolesByUserId_isNotRole() {
        var userId = 5L;

        var roles = this.roleRepository.findRolesByUserId(userId);

        assertThat(roles.size()).isEqualTo(0);
    }
}
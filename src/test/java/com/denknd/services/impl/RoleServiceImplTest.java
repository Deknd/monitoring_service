package com.denknd.services.impl;

import com.denknd.entity.Role;
import com.denknd.port.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RoleServiceImplTest {

    private RoleServiceImpl roleService;
    private RoleRepository roleRepository;
    @BeforeEach
    void setUp() {
        this.roleRepository = mock(RoleRepository.class);
        this.roleService = new RoleServiceImpl(this.roleRepository);
    }

    @Test
    @DisplayName("Проверяет, что передает в репозиторий только указанные в сервисе роли")
    void addRole() {
        var userId = 1L;
        var roleUser = Role.builder().roleName("USER").build();
        var roleAdmin = Role.builder().roleName("ADMIN").build();
        var roleRandom = Role.builder().roleName("RANDOM").build();

        this.roleService.addRoles(userId, roleUser, roleAdmin, roleRandom);

        var roleArrayCaptor = ArgumentCaptor.forClass(Role[].class);
        verify(this.roleRepository, times(1)).save(eq(userId), roleArrayCaptor.capture());
        var value = roleArrayCaptor.getValue();
        assertThat(value).contains(roleUser).contains(roleAdmin).doesNotContain(roleRandom);

    }
    @Test
    @DisplayName("Проверяет, что если роль не известна, возвращает пустой лист")
    void addRole_emptyList() {
        var userId = 1L;
        var roleRandom = Role.builder().roleName("RANDOM").build();

        var result = this.roleService.addRoles(userId, roleRandom);

        verify(this.roleRepository, times(0)).save(any(),any());
        assertThat(result).isFalse();
    }
    @Test
    @DisplayName("Проверяет, что сервис обращается в репозиторий")
    void getRoles(){
        var userId = 1L;

        this.roleService.getRoles(userId);

        verify(this.roleRepository, times(1)).findRolesByUserId(eq(userId));
    }
    @Test
    @DisplayName("Проверяет наличия дефолтных ролей")
    void getDefaultRole(){
        var roleUser = Role.builder().roleName("USER").build();
        var roleADMIN = Role.builder().roleName("ADMIN").build();

        var defaultRole = this.roleService.getDefaultRole();

        assertThat(defaultRole).contains(roleUser, roleADMIN);

    }
}
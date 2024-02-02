package com.denknd.services.impl;

import com.denknd.entity.Roles;
import com.denknd.repository.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class RoleServiceImplTest {

  private RoleServiceImpl roleService;
  @Mock
  private RoleRepository roleRepository;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.roleService = new RoleServiceImpl(this.roleRepository);
  }

  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }

  @Test
  @DisplayName("Проверяет, что передает в репозиторий только указанные в сервисе роли")
  void addRole() {
    var userId = 1L;
    var roleUser = Roles.USER;

    this.roleService.addRoles(userId, roleUser);

    var roleArrayCaptor = ArgumentCaptor.forClass(Roles.class);
    verify(this.roleRepository, times(1)).save(eq(userId), roleArrayCaptor.capture());
    var value = roleArrayCaptor.getValue();
    assertThat(value).isEqualTo(roleUser);

  }
  
  @Test
  @DisplayName("Проверяет, что сервис обращается в репозиторий")
  void getRoles() {
    var userId = 1L;

    this.roleService.getRoles(userId);

    verify(this.roleRepository, times(1)).findRolesByUserId(eq(userId));
  }
}
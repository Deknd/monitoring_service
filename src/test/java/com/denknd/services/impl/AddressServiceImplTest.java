package com.denknd.services.impl;

import com.denknd.entity.Address;
import com.denknd.entity.Roles;
import com.denknd.exception.AddressDatabaseException;
import com.denknd.repository.AddressRepository;
import com.denknd.security.entity.UserSecurity;
import com.denknd.security.service.SecurityService;
import com.denknd.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AddressServiceImplTest {
  @Mock
  private AddressRepository addressRepository;
  @Mock
  private SecurityService securityService;
  @Mock
  private UserService userService;
  private AddressServiceImpl addressService;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.addressService = new AddressServiceImpl(this.addressRepository, this.securityService, this.userService);
  }

  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }

  @Test
  @DisplayName("Проверяет, что при вызове с ролью USER, сервис обращается в репозиторий с айди авторизированного пользователя")
  void getAddresses() {
    var userId = 1L;
    var userSecurity = mock(UserSecurity.class);
    when(userSecurity.role()).thenReturn(Roles.USER);
    when(userSecurity.userId()).thenReturn(userId);
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);

    this.addressService.getAddresses(Optional.of(5L));

    verify(this.addressRepository, times(1)).findAddressByUserId(eq(userId));
  }

  @Test
  @DisplayName("Проверяет, что при вызове с ролью ADMIN, сервис обращается в репозиторий с айди переданным в параметрах")
  void getAddresses_Admin() {
    var userId = 1L;
    var userSecurity = mock(UserSecurity.class);
    when(userSecurity.role()).thenReturn(Roles.ADMIN);
    when(userSecurity.userId()).thenReturn(userId);
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);

    var id = 5L;
    this.addressService.getAddresses(Optional.of(id));

    verify(this.addressRepository, times(1)).findAddressByUserId(eq(id));
  }

  @Test
  @DisplayName("Проверяет, что при вызове с ролью ADMIN и не передаче в параметрах айди, возвращается пустой лист")
  void getAddresses_AdminNotParam() {
    var userId = 1L;
    var userSecurity = mock(UserSecurity.class);
    when(userSecurity.role()).thenReturn(Roles.ADMIN);
    when(userSecurity.userId()).thenReturn(userId);
    when(this.securityService.isAuthentication()).thenReturn(true);
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);

    var addresses = this.addressService.getAddresses(Optional.empty());

    assertThat(addresses).isEmpty();
    verify(this.addressRepository, times(0)).findAddressByUserId(any());
  }

  @Test
  @DisplayName("Проверяет, что при вызове не авторизованным, возвращается пустой лист")
  void getAddresses_notAuth() {
    when(this.securityService.isAuthentication()).thenReturn(false);

    var addresses = this.addressService.getAddresses(Optional.empty());

    assertThat(addresses).isEmpty();
    verify(this.addressRepository, times(0)).findAddressByUserId(any());
  }


  @Test
  @DisplayName("Проверяет, что сервис обращается в репозиторий")
  void addAddressByUser() throws AddressDatabaseException, SQLException, AccessDeniedException {
    var address = mock(Address.class);
    var userSecurity = UserSecurity.builder().userId(3L).role(Roles.USER).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);

    this.addressService.addAddressByUser(address);

    verify(this.addressRepository, times(1)).addAddress(eq(address));
  }

  @Test
  @DisplayName("Проверяет, что сервис обращается в репозиторий и репозиторий выкидывает ошибку")
  void addAddressByUser_sqlException() throws SQLException {
    var address = mock(Address.class);
    var userSecurity = UserSecurity.builder().userId(3L).role(Roles.USER).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);
    when(this.addressRepository.addAddress(any())).thenThrow(SQLException.class);

    assertThatThrownBy(() -> this.addressService.addAddressByUser(address)).isInstanceOf(AddressDatabaseException.class);

    verify(this.addressRepository, times(1)).addAddress(eq(address));
  }

  @Test
  @DisplayName("Проверяет, что с ролью админ сразу выкидывает ошибку")
  void addAddressByUser_admin() throws SQLException {
    var address = mock(Address.class);
    var userSecurity = UserSecurity.builder().userId(3L).role(Roles.ADMIN).build();
    when(this.securityService.getUserSecurity()).thenReturn(userSecurity);

    assertThatThrownBy(() -> this.addressService.addAddressByUser(address)).isInstanceOf(AccessDeniedException.class);

    verify(this.addressRepository, times(0)).addAddress(any());
  }

  @Test
  @DisplayName("Проверяет, что сервис обращается в репозиторий")
  void getAddressByAddressId() {
    var addressId = 1L;

    this.addressService.getAddressByAddressId(addressId);

    verify(this.addressRepository, times(1)).findAddress(eq(addressId));
  }
}
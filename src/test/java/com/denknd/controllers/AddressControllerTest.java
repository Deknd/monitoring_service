package com.denknd.controllers;

import com.denknd.dto.AddressDto;
import com.denknd.entity.Address;
import com.denknd.mappers.AddressMapper;
import com.denknd.services.AddressService;
import com.denknd.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AddressControllerTest {

  @Mock
  private AddressService addressService;
  @Mock
  private UserService userService;
  @Mock
  private AddressMapper addressMapper;
  private AddressController addressController;
  private AutoCloseable closeable;


  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.addressController = new AddressController(this.addressService, this.userService, this.addressMapper);
  }

  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }

  @Test
  @DisplayName("Проверяет, что вызываются нужные сервисы, с нужными аргументами")
  void addAddress() {
    var addressDto = mock(AddressDto.class);
    var userId = 1L;
    var address = mock(Address.class);
    when(this.addressMapper.mapAddressDtoToAddress(eq(addressDto))).thenReturn(address);

    this.addressController.addAddress(addressDto, userId);
    verify(this.userService, times(1)).getUserById(eq(userId));
    verify(address, times(1)).setOwner(any());
    verify(this.addressService, times(1)).addAddressByUser(any());
  }

  @Test
  @DisplayName("Проверяет, что вызываются нужные сервисы, с нужными аргументами")
  void getAddress() {
    var userId = 1L;

    this.addressController.getAddress(userId);

    verify(this.addressService, times(1)).getAddresses(eq(userId));
  }
}
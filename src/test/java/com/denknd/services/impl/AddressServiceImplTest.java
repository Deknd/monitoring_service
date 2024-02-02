package com.denknd.services.impl;

import com.denknd.entity.Address;
import com.denknd.repository.AddressRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class AddressServiceImplTest {
    @Mock
    private AddressRepository addressRepository;
    private AddressServiceImpl addressService;
    private AutoCloseable closeable;
    @BeforeEach
    void setUp() {
        this.closeable = MockitoAnnotations.openMocks(this);
        addressService = new AddressServiceImpl(addressRepository);
    }
    @AfterEach
    void tearDown() throws Exception {
        this.closeable.close();
    }
    @Test
    @DisplayName("Проверяет, что сервис обращается в репозиторий")
    void getAddresses() {
        var userId = 1L;

        this.addressService.getAddresses(userId);

        verify(this.addressRepository, times(1)).findAddressByUserId(eq(userId));
    }
    @Test
    @DisplayName("Проверяет, что не обращается в сервис, если нет пользователя")
    void getAddresses_notUser() {

        this.addressService.getAddresses(null);

        verify(this.addressRepository, times(0)).findAddressByUserId(any());
    }
    @Test
    @DisplayName("Проверяет, что сервис обращается в репозиторий")
    void addAddressByUser() {
        var address = mock(Address.class);

        this.addressService.addAddressByUser(address);

        verify(this.addressRepository, times(1)).addAddress(eq(address));
    }
    @Test
    @DisplayName("Проверяет, что сервис обращается в репозиторий")
    void getAddressByAddressId() {
        var addressId = 1L;

        this.addressService.getAddressByAddressId(addressId);

        verify(this.addressRepository, times(1)).findAddress(eq(addressId));
    }
}
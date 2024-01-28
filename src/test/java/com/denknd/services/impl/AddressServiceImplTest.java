package com.denknd.services.impl;

import com.denknd.port.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddressServiceImplTest {

    private AddressRepository addressRepository;
    private AddressServiceImpl addressService;
    @BeforeEach
    void setUp() {
        addressRepository = mock(AddressRepository.class);
        addressService = new AddressServiceImpl(addressRepository);
    }

    @Test
    @DisplayName("Проверяет, что сервис обращается в репозиторий")
    void getAddresses() {
        var userId = 1L;

        this.addressService.getAddresses(userId);

        verify(this.addressRepository, times(1)).findAddressByUserId(eq(userId));
    }
}
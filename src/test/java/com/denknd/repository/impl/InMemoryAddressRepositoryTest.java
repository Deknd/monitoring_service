package com.denknd.repository.impl;

import com.denknd.entity.Address;
import com.denknd.entity.User;
import com.denknd.repository.impl.InMemoryAddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InMemoryAddressRepositoryTest {

    private InMemoryAddressRepository addressRepository;

    private Address addressToMemory;
    @BeforeEach
    void setUp() {
        this.addressRepository = new InMemoryAddressRepository();
        var owner = mock(User.class);
        var address = Address.builder().owner(owner).build();

        when(owner.getUserId()).thenReturn(1L);
        this.addressToMemory = this.addressRepository.addAddress(address);
    }

    @Test
    @DisplayName("Проверяет, что сохраняется адрес в репозиторий")
    void addAddress() {
        var owner = mock(User.class);
        var address = Address.builder().owner(owner).build();

        when(owner.getUserId()).thenReturn(2L);


        var result = this.addressRepository.addAddress(address);

       assertThat(result.getAddressId()).isNotNull();
        var exist = this.addressRepository.findAddressByUserId(2L);
        assertThat(exist).isNotEmpty();
    }

    @Test
    @DisplayName("Проверяет, что сохраняется адрес в репозиторий, когда существует еще один адрес у данного пользователя")
    void addAddress_addAddressToList() {
        var owner = mock(User.class);
        var address = Address.builder().owner(owner).build();
        when(owner.getUserId()).thenReturn(1L);

        var result = this.addressRepository.addAddress(address);

        assertThat(result.getAddressId()).isNotNull();

        var exist = this.addressRepository.findAddressByUserId(1L);
        assertThat(exist).isNotEmpty();
        assertThat(exist.size()).isEqualTo(2);
    }
    @Test
    @DisplayName("Проверяет, что не сохраняется адрес у которого есть id")
    void addAddress_oldAddress() {
        var owner = mock(User.class);
        var address = Address.builder().owner(owner).addressId(12L).build();

        var userId = 5L;
        when(owner.getUserId()).thenReturn(userId);


        var result = this.addressRepository.addAddress(address);

        assertThat(result).isNull();
        var exist = this.addressRepository.findAddressByUserId(userId);
        assertThat(exist).isEmpty();
    }

    @Test
    @DisplayName("Проверяет, что находится адреса у пользователя")
    void findAddressByUserId() {
        var userId = 1L;

        var exist = this.addressRepository.findAddressByUserId(userId);

        assertThat(exist).isNotEmpty();
    }

    @Test
    @DisplayName("Проверяет, что не находится адреса у пользователя")
    void findAddressByUserId_notAddress() {
        var userId = 4L;

        var exist = this.addressRepository.findAddressByUserId(userId);

        assertThat(exist).isEmpty();
    }

    @Test
    @DisplayName("ищет адрес по идентификатору адреса")
    void findAddress(){
        var addressId = this.addressToMemory.getAddressId();

        var addressOptional = this.addressRepository.findAddress(addressId);

        assertThat(addressOptional).isPresent();
        var address = addressOptional.get();
        assertThat(address.getAddressId()).isEqualTo(addressId);

    }

    @Test
    @DisplayName("не находит адреса, по идентификатору ")
    void findAddress_notAddress(){
        var addressId = 234234L;

        var addressOptional = this.addressRepository.findAddress(addressId);

        assertThat(addressOptional).isEmpty();
    }
}
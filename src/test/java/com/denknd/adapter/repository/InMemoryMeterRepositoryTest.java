package com.denknd.adapter.repository;

import com.denknd.entity.Address;
import com.denknd.entity.MeterReading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InMemoryMeterRepositoryTest {

    private InMemoryMeterRepository meterRepository;
    @BeforeEach
    void setUp() {
        var address = mock(Address.class);
        when(address.getAddressId()).thenReturn(2L);
        var meterReading = MeterReading.builder().address(address).build();
        var meterReading2 = MeterReading.builder().address(address).build();
        this.meterRepository = new InMemoryMeterRepository();
        this.meterRepository.save(meterReading);
        this.meterRepository.save(meterReading2);
    }

    @Test
    @DisplayName("Проверяет что объект сохраняется и генерирует id")
    void save() {
        var address = mock(Address.class);
        when(address.getAddressId()).thenReturn(1L);
        var meterReading = MeterReading.builder().address(address).build();

        var save = this.meterRepository.save(meterReading);
        assertThat(save.getMeterId()).isNotNull();
    }
    @Test
    @DisplayName("Проверяет что при попытки обновить данные в показаниях, возвращается null")
    void save_attemptUpdate() {
        var address = mock(Address.class);
        when(address.getAddressId()).thenReturn(1L);
        var meterReading = MeterReading.builder().meterId(123L).address(address).build();

        var save = this.meterRepository.save(meterReading);
        assertThat(save).isNull();
    }
    @Test
    @DisplayName("Проверяет что при попытки добавить данные к одному и тому же данному")
    void save_addsDataSingleAddress() {
        var address = mock(Address.class);
        when(address.getAddressId()).thenReturn(2L);
        var meterReading = MeterReading.builder().address(address).build();

        var save = this.meterRepository.save(meterReading);
        assertThat(save.getMeterId()).isNotNull();
    }

    @Test
    @DisplayName("Достает из бд по адресу список показаний")
    void findMeterReadingByAddressId() {
        var addressId = 2L;

        var meterReadingByAddressId = this.meterRepository.findMeterReadingByAddressId(addressId);

        assertThat(meterReadingByAddressId.size()).isEqualTo(2);

    }
    @Test
    @DisplayName("Достает из бд по адресу список показаний")
    void findMeterReadingByAddressId_notAddress() {
        var addressId = 23L;

        var meterReadingByAddressId = this.meterRepository.findMeterReadingByAddressId(addressId);

        assertThat(meterReadingByAddressId.isEmpty()).isTrue();

    }
}
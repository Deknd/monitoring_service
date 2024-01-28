package com.denknd.services.impl;

import com.denknd.entity.Address;
import com.denknd.port.AddressRepository;
import com.denknd.services.AddressService;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Сервис для работы с адресами
 */
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    /**
     * Репозиторий для работы с адресами
     */
    private final AddressRepository addressRepository;

    /**
     * запрашивает в репозитории все адреса по данному айди
     * @param userId айди пользователя
     * @return список адресов
     */
    @Override
    public List<Address> getAddresses(Long userId) {
        return this.addressRepository.findAddressByUserId(userId);
    }

    /**
     * Передает в репозиторий объект адреса, для сохранения
     * @param address полностью заполненный объект адреса без айди
     * @return полностью заполненный объект адреса с айди
     */
    @Override
    public Address addAddressByUser(Address address) {
        return this.addressRepository.addAddress(address);
    }
}

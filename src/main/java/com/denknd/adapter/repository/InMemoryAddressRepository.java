package com.denknd.adapter.repository;

import com.denknd.entity.Address;
import com.denknd.port.AddressRepository;

import java.util.*;

/**
 * Класс для хранения адресов
 */
public class InMemoryAddressRepository implements AddressRepository {

    /**
     * Мапа хранящая связь userId со списком addressId
     */
    private final Map<Long, List<Long>> addressUserRelationship = new HashMap<>();
    /**
     * Мапа хранящая связь addressId с объектом Address
     */
    private final Map<Long, Address> addressIdToAddress = new HashMap<>();
    /**
     * Для генерации addressId
     */
    private final Random random = new Random();

    /**
     * Сохраняет в память Address
     * @param address Полностью заполненный, кроме addressId. Если передать с заполненным addressId, сохранение не произойдет
     * @return возвращает полностью заполненный объект Address
     */
    @Override
    public Address addAddress(Address address) {
        var userId = address.getOwner().getUserId();
        long addressId;
        if (address.getAddressId() == null) {

            do {
                addressId = Math.abs(random.nextLong());

            } while (this.addressIdToAddress.containsKey(addressId));
        } else {
            return null;
        }
        address.setAddressId(addressId);
        addressIdToAddress.put(addressId, address);

        if (this.addressUserRelationship.containsKey(userId)) {
            var addresses = this.addressUserRelationship.get(userId);
            addresses.add(addressId);
            this.addressUserRelationship.put(userId, addresses);
        } else {

            var addresses = new ArrayList<Long>();
            addresses.add(addressId);
            this.addressUserRelationship.put(userId, addresses);
        }
        return buildAddress(address);
    }

    /**
     * Собирает новый объект Address
     * @param address заполненный объект
     * @return новый объект
     */
    private Address buildAddress(Address address) {
        return Address.builder()
                .addressId(address.getAddressId())
                .owner(address.getOwner())
                .postalCode(address.getPostalCode())
                .region(address.getRegion())
                .city(address.getCity())
                .street(address.getStreet())
                .house(address.getHouse())
                .apartment(address.getApartment())
                .meterReadings(address.getMeterReadings())
                .build();
    }

    /**
     * Ищет все адреса по айди пользователя
     * @param userId идентификатор пользователя
     * @return возвращает список адресов или пустой лист, если нет адресов
     */
    @Override
    public List<Address> findAddressByUserId(Long userId) {
        var addressIdList = this.addressUserRelationship.getOrDefault(userId, List.of());
        if(addressIdList.isEmpty()){
            return List.of();
        }
        return addressIdList.stream().map(this.addressIdToAddress::get).map(this::buildAddress).toList();
    }
}

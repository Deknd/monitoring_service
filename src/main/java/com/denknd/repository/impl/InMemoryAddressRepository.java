package com.denknd.repository.impl;

import com.denknd.entity.Address;
import com.denknd.repository.AddressRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * Реализация интерфейса для хранения адресов в памяти.
 */
public class InMemoryAddressRepository implements AddressRepository {

  /**
   * Связь userId с списком addressId.
   */
  private final Map<Long, List<Long>> userAddressMap = new HashMap<>();
  /**
   * Связь addressId с объектом Address.
   */
  private final Map<Long, Address> addressMap = new HashMap<>();
  /**
   * Генератор addressId.
   */
  private final Random random = new Random();

  /**
   * Добавляет новый адрес в хранилище.
   *
   * @param address Полностью заполненный, за исключением addressId.
   *                Если передан с заполненным addressId, сохранение не произойдет.
   * @return Полностью заполненный объект Address
   */
  @Override
  public Address addAddress(Address address) {
    long addressId;
    if (address.getAddressId() == null) {

      do {
        addressId = Math.abs(random.nextLong());

      } while (this.addressMap.containsKey(addressId));
    } else {
      return null;
    }
    address.setAddressId(addressId);
    addressMap.put(addressId, address);
    var userId = address.getOwner().getUserId();
    userAddressMap.computeIfAbsent(userId, key -> new ArrayList<>()).add(addressId);
    return buildAddress(address);
  }

  /**
   * Собирает новый объект Address.
   *
   * @param address Заполненный объект Address
   * @return Новый объект Address
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
   * Находит все адреса по идентификатору пользователя.
   *
   * @param userId Идентификатор пользователя
   * @return Список адресов или пустой список, если адресов нет
   */
  @Override
  public List<Address> findAddressByUserId(Long userId) {
    var addressIdList = this.userAddressMap.getOrDefault(userId, List.of());
    if (addressIdList.isEmpty()) {
      return List.of();
    }
    return addressIdList.stream()
            .map(this.addressMap::get)
            .map(this::buildAddress).toList();
  }

  /**
   * Находит адрес по его идентификатору.
   *
   * @param addressId Идентификатор адреса
   * @return Optional с объектом Address, если адрес найден, иначе пустой Optional
   */
  @Override
  public Optional<Address> findAddress(Long addressId) {
    return Optional.ofNullable(addressMap.get(addressId)).map(this::buildAddress);
  }


}

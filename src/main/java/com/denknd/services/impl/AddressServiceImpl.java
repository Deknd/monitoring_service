package com.denknd.services.impl;

import com.denknd.entity.Address;
import com.denknd.exception.AddressDatabaseException;
import com.denknd.repository.AddressRepository;
import com.denknd.services.AddressService;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.util.List;

/**
 * Сервис для работы с адресами.
 */
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

  /**
   * Репозиторий для работы с адресами.
   */
  private final AddressRepository addressRepository;

  /**
   * Получает список адресов для указанного пользователя по его айди.
   *
   * @param userId Айди пользователя.
   * @return Список адресов пользователя или пустой список, если айди пользователя равен null.
   */
  @Override
  public List<Address> getAddresses(Long userId) {
    if (userId == null) {
      return List.of();
    }
    return this.addressRepository.findAddressByUserId(userId);
  }

  /**
   * Сохраняет новый адрес пользователя в репозитории.
   *
   * @param address Полностью заполненный объект адреса без айди.
   * @return Полностью заполненный объект адреса с присвоенным айди.
   */
  @Override
  public Address addAddressByUser(Address address) throws AddressDatabaseException {
    try {
      return this.addressRepository.addAddress(address);
    } catch (SQLException e) {
      throw new AddressDatabaseException("Данные переданные для сохранения адреса не валидны: " + e.getMessage());
    }
  }

  /**
   * Возвращает адрес по его айди.
   *
   * @param addressId Айди адреса.
   * @return Объект адреса, если найден, или null, если адрес не существует.
   */
  @Override
  public Address getAddressByAddressId(Long addressId) {
    return this.addressRepository.findAddress(addressId).orElse(null);
  }
}

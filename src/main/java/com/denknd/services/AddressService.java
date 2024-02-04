package com.denknd.services;

import com.denknd.entity.Address;
import com.denknd.exception.AddressDatabaseException;

import java.util.List;

/**
 * Интерфейс сервиса для взаимодействия с адресами.
 */
public interface AddressService {
  /**
   * Получение списка доступных адресов для пользователя.
   *
   * @param userId Идентификатор пользователя.
   * @return Список доступных адресов.
   */
  List<Address> getAddresses(Long userId);

  /**
   * Добавление адреса для пользователя.
   *
   * @param address Заполненный объект адреса без идентификатора.
   * @return Копия сохраненного адреса с идентификатором.
   */
  Address addAddressByUser(Address address) throws AddressDatabaseException;
  /**
   * Получение адреса по его идентификатору.
   *
   * @param addressId Идентификатор адреса.
   * @return Заполненный объект адреса или null, если не найден.
   */
  Address getAddressByAddressId(Long addressId);
}

package com.denknd.services;

import com.denknd.entity.Address;
import com.denknd.exception.AddressDatabaseException;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

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
  List<Address> getAddresses(Optional<Long> userId);

  /**
   * Добавление адреса для пользователя.
   *
   * @param address Заполненный объект адреса без идентификатора.
   * @return Копия сохраненного адреса с идентификатором.
   * @throws AddressDatabaseException   Исключение, выбрасываемое в случае ошибки базы данных при добавлении адреса.
   * @throws AccessDeniedException      Исключение, выбрасываемое в случае отсутствия доступа для добавления адреса.
   */
  Address addAddressByUser(Address address) throws AddressDatabaseException, AccessDeniedException;

  /**
   * Получение адреса по его идентификатору.
   *
   * @param addressId Идентификатор адреса.
   * @return Заполненный объект адреса или null, если не найден.
   */
  Address getAddressByAddressId(Long addressId);
}

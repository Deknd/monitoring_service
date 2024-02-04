package com.denknd.repository;

import com.denknd.entity.Address;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для управления адресами.
 */
public interface AddressRepository {
  /**
   * Сохраняет адрес в репозиторий и возвращает объект с присвоенным идентификатором.
   *
   * @param address Объект адреса, не содержащий идентификатор.
   * @return Объект адреса с присвоенным идентификатором.
   * @throws SQLException выкидывается, если не соблюдены ограничения БД
   */
  Address addAddress(Address address) throws SQLException;

  /**
   * Ищет все адреса, доступные пользователю с указанным идентификатором.
   *
   * @param userId Идентификатор пользователя.
   * @return Список всех доступных адресов или пустой список, если адресов нет.
   */
  List<Address> findAddressByUserId(Long userId);

  /**
   * Ищет адрес по указанному идентификатору.
   *
   * @param addressId Идентификатор адреса.
   * @return Optional с найденным адресом или пустой Optional, если адрес не найден.
   */
  Optional<Address> findAddress(Long addressId);
}

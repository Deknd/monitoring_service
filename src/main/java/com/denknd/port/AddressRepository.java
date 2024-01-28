package com.denknd.port;

import com.denknd.entity.Address;

import java.util.List;

/**
 * Интерфейс репозитория Адресов
 */
public interface AddressRepository {
    /**
     * Сохраняет адрес в бд и выдает идентификатор
     * @param address заполярный объект адреса, без айди
     * @return возвращает объект адреса, с айди
     */
    Address addAddress(Address address);

    /**
     * Находит все адреса доступные пользователю с данным айди
     * @param userId айди пользователя
     * @return все доступные адреса или пустой список, если таких нет
     */
    List<Address> findAddressByUserId(Long userId);
}

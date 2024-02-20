package com.denknd.mappers;

import com.denknd.dto.AddressDto;
import com.denknd.entity.Address;
import com.denknd.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Маппер для объекта Address
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AddressMapper {
  AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

  /**
   * Преобразование AddressDto в Address
   *
   * @param addressDto адрес от пользователя
   * @return адрес для сервиса
   */
  Address mapAddressDtoToAddress(AddressDto addressDto);

  /**
   * Преобразование Address в AddressDto
   *
   * @param address адрес от сервиса
   * @return адрес для пользователя
   */
  AddressDto mapAddressToAddressDto(Address address);

  /**
   * Преобразование списка Address в список AddressDto
   *
   * @param addresses адреса от сервиса
   * @return адреса для пользователя
   */
  List<AddressDto> mapAddressesToAddressesDto(List<Address> addresses);

  /**
   * Собирает новый объект Address.
   * @param resultSet данные полученные из БД
   * @return Заполненный объект Address
   * @throws SQLException ошибка получения данных
   */
  default Address mapResultSetToAddress(ResultSet resultSet) throws SQLException {
    return Address.builder()
            .addressId(resultSet.getLong("address_id"))
            .owner(User.builder().userId(resultSet.getLong("user_id")).build())
            .postalCode(resultSet.getLong("postal_code"))
            .region(resultSet.getString("region"))
            .city(resultSet.getString("city"))
            .street(resultSet.getString("street"))
            .house(resultSet.getString("house"))
            .apartment(resultSet.getString("apartment"))
            .build();
  }

}

package com.denknd.mappers;

import com.denknd.dto.AddressDto;
import com.denknd.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

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

}

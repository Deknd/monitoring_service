package com.denknd.controllers;

import com.denknd.aspectj.audit.AuditRecording;
import com.denknd.dto.AddressDto;
import com.denknd.exception.AddressDatabaseException;
import com.denknd.mappers.AddressMapper;
import com.denknd.services.AddressService;
import com.denknd.services.UserService;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Контроллер для работы с адресами.
 */
@RequiredArgsConstructor
public class AddressController {
  /**
   * Сервис управления адресами.
   */
  private final AddressService addressService;
  /**
   * Сервис управления пользователями.
   */
  private final UserService userService;
  /**
   * Маппер адресов.
   */
  private final AddressMapper addressMapper;

  /**
   * Добавляет адрес пользователю.
   *
   * @param addressDto адрес полученный от пользователя
   * @param userId     идентификатор пользователя, к которому нужно добавить адрес
   * @return возвращает добавленный адрес
   */
  @AuditRecording("Добавляет новый адрес")
  public AddressDto addAddress(AddressDto addressDto, Long userId) throws AddressDatabaseException {
    var address = this.addressMapper.mapAddressDtoToAddress(addressDto);
    var user = this.userService.getUserById(userId);
    address.setOwner(user);
    var result = this.addressService.addAddressByUser(address);
    return this.addressMapper.mapAddressToAddressDto(result);
  }

  /**
   * Возвращает адреса пользователю, по его идентификатору.
   *
   * @param userId идентификатор пользователя
   * @return адреса доступные пользователю
   */
  @AuditRecording("Получения списка адресов")
  public List<AddressDto> getAddress(Long userId) {
    var addresses = this.addressService.getAddresses(userId);
    return this.addressMapper.mapAddressesToAddressesDto(addresses);
  }
}

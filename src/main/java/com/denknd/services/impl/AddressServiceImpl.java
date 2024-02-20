package com.denknd.services.impl;

import com.denknd.entity.Address;
import com.denknd.entity.Parameters;
import com.denknd.entity.Roles;
import com.denknd.exception.AddressDatabaseException;
import com.denknd.repository.AddressRepository;
import com.denknd.security.service.SecurityService;
import com.denknd.services.AddressService;
import com.denknd.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с адресами.
 */
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

  /**
   * Репозиторий для работы с адресами.
   */
  private final AddressRepository addressRepository;
  /**
   * Сервис управления безопасностью.
   */
  private final SecurityService securityService;
  /**
   * Сервис управления пользователями.
   */
  private final UserService userService;

  /**
   * Получает список адресов для указанного пользователя по его айди.
   *
   * @param userId Айди пользователя.
   * @return Список адресов пользователя или пустой список, если айди пользователя равен null.
   */
  @Override
  public List<Address> getAddresses(Optional<Long> userId) {
    if (!this.securityService.isAuthentication()){
      return Collections.emptyList();
    }
    var userSecurity = this.securityService.getUserSecurity();
    if (userSecurity.role().equals(Roles.USER)) {
      return this.addressRepository.findAddressByUserId(userSecurity.userId());
    }
    if (userSecurity.role().equals(Roles.ADMIN)) {
      if (userId.isEmpty()) {
        return Collections.emptyList();
      }
      return this.addressRepository.findAddressByUserId(userId.get());
    }
    return Collections.emptyList();
  }

  /**
   * Сохраняет новый адрес пользователя в репозитории.
   *
   * @param address Полностью заполненный объект адреса без айди.
   * @return Полностью заполненный объект адреса с присвоенным айди.
   */
  @Override
  public Address addAddressByUser(Address address) throws AddressDatabaseException, AccessDeniedException {
    var userSecurity = this.securityService.getUserSecurity();
    if (userSecurity.role().equals(Roles.USER)) {
      var user = this.userService.getUser(Parameters.builder().userId(userSecurity.userId()).build());
      address.setOwner(user);
      try {
        return this.addressRepository.addAddress(address);
      } catch (SQLException e) {
        throw new AddressDatabaseException("Данные переданные для сохранения адреса не валидны: " + e.getMessage());
      }
    }
    throw new AccessDeniedException(
            "Добавлять адреса может пользователь с ролью USER. "
                    + "Ваша роль: " + securityService.getUserSecurity().role().name());
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

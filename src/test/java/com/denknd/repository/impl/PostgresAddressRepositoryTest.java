package com.denknd.repository.impl;

import com.denknd.config.ContainerConfig;
import com.denknd.entity.Address;
import com.denknd.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.security.SecureRandom;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {ContainerConfig.class})
@ActiveProfiles("test")
class PostgresAddressRepositoryTest {
  @Autowired
  private PostgresAddressRepository addressRepository;
  private static SecureRandom random = new SecureRandom();
  private static String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

  @Test
  @DisplayName("Проверяет, что сохраняется адрес в репозиторий")
  void addAddress() throws SQLException {
    var owner = mock(User.class);
    var address = Address.builder()
            .owner(owner)
            .postalCode(123456L)
            .region("Мурманская область")
            .city("Кандалакша")
            .street("Восточная")
            .house("8")
            .apartment("2в")
            .build();
    when(owner.getUserId()).thenReturn(2L);

    var result = this.addressRepository.addAddress(address);

    assertThat(result.getAddressId()).isNotNull();
    var exist = this.addressRepository.findAddressByUserId(2L);
    assertThat(exist).isNotEmpty();
  }

  @Test
  @DisplayName("Проверяет, что сохраняется адрес в репозиторий")
  void addAddress_notValidPostalCode() throws SQLException {
    var owner = mock(User.class);
    var address = Address.builder()
            .owner(owner)
            .postalCode(1234567L)
            .region("Мурманская область")
            .city("Кандалакша")
            .street("Восточная")
            .house("8")
            .apartment("2в")
            .build();
    when(owner.getUserId()).thenReturn(2L);

    assertThatThrownBy(() -> this.addressRepository.addAddress(address));

  }

  @Test
  @DisplayName("Проверяет, что сохраняется адрес в репозиторий")
  void addAddress_longRegion() throws SQLException {
    var owner = mock(User.class);
    var address = Address.builder()
            .owner(owner)
            .postalCode(123456L)
            .region(generateRandomLogin(51))
            .city("Кандалакша")
            .street("Восточная")
            .house("8")
            .apartment("2в")
            .build();
    when(owner.getUserId()).thenReturn(2L);

    assertThatThrownBy(() -> this.addressRepository.addAddress(address));
  }

  @Test
  @DisplayName("Проверяет, что сохраняется адрес в репозиторий")
  void addAddress_longCity() throws SQLException {
    var owner = mock(User.class);
    var address = Address.builder()
            .owner(owner)
            .postalCode(123456L)
            .region("Мурманская область")
            .city(generateRandomLogin(51))
            .street("Восточная")
            .house("8")
            .apartment("2в")
            .build();
    when(owner.getUserId()).thenReturn(2L);

    assertThatThrownBy(() -> this.addressRepository.addAddress(address));
  }

  @Test
  @DisplayName("Проверяет, что сохраняется адрес в репозиторий")
  void addAddress_longStreet() throws SQLException {
    var owner = mock(User.class);
    var address = Address.builder()
            .owner(owner)
            .postalCode(123456L)
            .region("Мурманская область")
            .city("Кандалакша")
            .street(generateRandomLogin(51))
            .house("8")
            .apartment("2в")
            .build();
    when(owner.getUserId()).thenReturn(2L);

    assertThatThrownBy(() -> this.addressRepository.addAddress(address));
  }

  @Test
  @DisplayName("Проверяет, что сохраняется адрес в репозиторий")
  void addAddress_longHouse() throws SQLException {
    var owner = mock(User.class);
    var address = Address.builder()
            .owner(owner)
            .postalCode(123456L)
            .region("Мурманская область")
            .city("Кандалакша")
            .street("Восточная")
            .house(generateRandomLogin(6))
            .apartment("2в")
            .build();
    when(owner.getUserId()).thenReturn(2L);

    assertThatThrownBy(() -> this.addressRepository.addAddress(address));
  }

  @Test
  @DisplayName("Проверяет, что сохраняется адрес в репозиторий")
  void addAddress_longApartment() throws SQLException {
    var owner = mock(User.class);
    var address = Address.builder()
            .owner(owner)
            .postalCode(123456L)
            .region("Мурманская область")
            .city("Кандалакша")
            .street("Восточная")
            .house("8")
            .apartment(generateRandomLogin(6))
            .build();
    when(owner.getUserId()).thenReturn(2L);

    assertThatThrownBy(() -> this.addressRepository.addAddress(address));
  }

  @Test
  @DisplayName("Проверяет, что сохраняется адрес в репозиторий, когда существует еще один адрес у данного пользователя")
  void addAddress_addAddressToList() throws SQLException {
    var owner = mock(User.class);
    var address = Address.builder()
            .owner(owner)
            .postalCode(123456L)
            .region("Мурманская область")
            .city("Кандалакша")
            .street("Восточная")
            .house("8")
            .apartment("2в")
            .build();
    when(owner.getUserId()).thenReturn(1L);

    var result = this.addressRepository.addAddress(address);

    assertThat(result.getAddressId()).isNotNull();

    var exist = this.addressRepository.findAddressByUserId(1L);
    assertThat(exist).isNotEmpty();
  }

  @Test
  @DisplayName("Проверяет, что не сохраняется адрес у которого есть id")
  void addAddress_oldAddress() throws SQLException {
    var owner = mock(User.class);
    var address = Address.builder()
            .addressId(123L)
            .owner(owner)
            .postalCode(123456L)
            .region("Мурманская область")
            .city("Кандалакша")
            .street("Восточная")
            .house("8")
            .apartment("2в")
            .build();
    var userId = 5L;
    when(owner.getUserId()).thenReturn(userId);


    assertThatThrownBy(() -> this.addressRepository.addAddress(address));

    var exist = this.addressRepository.findAddressByUserId(userId);
    assertThat(exist).isEmpty();
  }

  @Test
  @DisplayName("Проверяет, что находится адреса у пользователя")
  void findAddressByUserId() {
    var userId = 1L;

    var exist = this.addressRepository.findAddressByUserId(userId);

    assertThat(exist).isNotEmpty();
  }

  @Test
  @DisplayName("Проверяет, что не находится адреса у пользователя")
  void findAddressByUserId_notAddress() {
    var userId = 2351345L;

    var exist = this.addressRepository.findAddressByUserId(userId);

    assertThat(exist).isEmpty();
  }

  @Test
  @DisplayName("ищет адрес по идентификатору адреса")
  void findAddress() throws SQLException {
    var addressId = 1L;

    var addressOptional = this.addressRepository.findAddress(addressId);

    assertThat(addressOptional).isPresent();
    var address = addressOptional.get();
    assertThat(address.getOwner().getUserId()).isEqualTo(1L);
    assertThat(address.getAddressId()).isEqualTo(addressId);
    assertThat(address.getPostalCode()).isEqualTo(184040);
    assertThat(address.getRegion()).isEqualTo("Мурманская область");
    assertThat(address.getCity()).isEqualTo("Кандалакша");
    assertThat(address.getStreet()).isEqualTo("Восточная");
    assertThat(address.getHouse()).isEqualTo("6");
    assertThat(address.getApartment()).isEqualTo("3");

  }

  @Test
  @DisplayName("не находит адреса, по идентификатору ")
  void findAddress_notAddress() {
    var addressId = 234234L;

    var addressOptional = this.addressRepository.findAddress(addressId);
    assertThat(addressOptional).isEmpty();
  }

  public String generateRandomLogin(int length) {
    StringBuilder login = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
      login.append(ALLOWED_CHARACTERS.charAt(randomIndex));
    }
    return login.toString();
  }
}
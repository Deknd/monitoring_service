package com.denknd.repository.impl;

import com.denknd.entity.Address;
import com.denknd.mappers.AddressMapper;
import com.denknd.repository.AddressRepository;
import com.denknd.util.DataBaseConnection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса для хранения адресов в БД.
 */
@Repository
@RequiredArgsConstructor
public class PostgresAddressRepository implements AddressRepository {
  /**
   * Выдает соединение с базой данных
   */
  private final DataBaseConnection dataBaseConnection;
  /**
   * Маппер для мапинга адресов.
   */
  private final AddressMapper addressMapper;

  /**
   * Добавляет новый адрес в хранилище.
   *
   * @param address Полностью заполненный, за исключением addressId.
   *                Если передан с заполненным addressId, сохранение не произойдет.
   * @return Полностью заполненный объект Address
   * @throws SQLException выкидывается, если не соблюдены ограничения БД
   */
  @Override
  public Address addAddress(Address address) throws SQLException {
    if (address.getAddressId() != null) {
      throw new SQLException("Ошибка сохранения нового адреса. Переданный адрес содержит уже идентификатор");
    }
    var sql = "INSERT INTO addresses (user_id, postal_code, region, city, street, house, apartment) VALUES (?, ?, ?, ?, ?, ?, ?)";
    var connection = dataBaseConnection.createConnection();
    connection.setAutoCommit(false);
    try (var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      preparedStatement.setLong(1, address.getOwner().getUserId());
      preparedStatement.setLong(2, address.getPostalCode());
      preparedStatement.setString(3, address.getRegion());
      preparedStatement.setString(4, address.getCity());
      preparedStatement.setString(5, address.getStreet());
      preparedStatement.setString(6, address.getHouse());
      preparedStatement.setString(7, address.getApartment());
      int affectedRows = preparedStatement.executeUpdate();
      if (affectedRows == 0) {
        connection.rollback();
        throw new SQLException("Ошибка сохранения адреса, ни одной строки не добавлено");
      }
      try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          address.setAddressId(generatedKeys.getLong(1));
          connection.commit();
          return address;
        } else {
          connection.rollback();
          throw new SQLException("Ошибка сохранения адреса, не сгенерировано идентификатора");
        }
      }
    } finally {
      if (connection != null) {
        connection.close();
      }
    }
  }

  /**
   * Находит все адреса по идентификатору пользователя.
   *
   * @param userId Идентификатор пользователя
   * @return Список адресов или пустой список, если адресов нет
   */
  @Override
  public List<Address> findAddressByUserId(Long userId) {
    String sql = "SELECT * FROM addresses WHERE user_id = ?";

    try (var connection = dataBaseConnection.createConnection();
         var preparedStatement = connection.prepareStatement(sql)) {

      preparedStatement.setLong(1, userId);

      try (var resultSet = preparedStatement.executeQuery()) {
        var addresses = new ArrayList<Address>();

        while (resultSet.next()) {
          var address = this.addressMapper.mapResultSetToAddress(resultSet);
          addresses.add(address);
        }
        return addresses;
      }
    } catch (SQLException e) {
      return Collections.emptyList();
    }
  }

  /**
   * Находит адрес по его идентификатору.
   *
   * @param addressId Идентификатор адреса
   * @return Optional с объектом Address, если адрес найден, иначе пустой Optional
   */
  @Override
  public Optional<Address> findAddress(Long addressId) {
    var sql = "SELECT * FROM addresses WHERE address_id = ?";

    try (var connection = dataBaseConnection.createConnection();
         var preparedStatement = connection.prepareStatement(sql)) {

      preparedStatement.setLong(1, addressId);

      try (var resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          var address = this.addressMapper.mapResultSetToAddress(resultSet);
          return Optional.of(address);
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      return Optional.empty();
    }
  }

}

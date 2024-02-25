package com.denknd.repository.impl;

import com.denknd.entity.Address;
import com.denknd.mappers.AddressMapper;
import com.denknd.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса для хранения адресов в БД.
 */
@Repository
@RequiredArgsConstructor
public class PostgresAddressRepository implements AddressRepository {
  private final JdbcTemplate jdbcTemplate;
  private final AddressMapper addressMapper;

  /**
   * Добавляет новый адрес в хранилище.
   *
   * @param address Полностью заполненный, за исключением addressId.
   *                Если передан с заполненным addressId, сохранение не произойдет.
   * @return Полностью заполненный объект Address
   * @throws SQLException выкидывается, если не соблюдены ограничения БД
   */
  @Transactional
  @Override
  public Address addAddress(Address address) throws SQLException {
    if (address.getAddressId() != null) {
      throw new SQLException("Ошибка сохранения нового адреса. Переданный адрес содержит уже идентификатор");
    }
    var sql = "INSERT INTO addresses (user_id, postal_code, region, city, street, house, apartment) VALUES (?, ?, ?, ?, ?, ?, ?)";
    var keyHolder = new GeneratedKeyHolder();
    var affectedRows = jdbcTemplate.update(connection -> {
      var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setLong(1, address.getOwner().getUserId());
      preparedStatement.setLong(2, address.getPostalCode());
      preparedStatement.setString(3, address.getRegion());
      preparedStatement.setString(4, address.getCity());
      preparedStatement.setString(5, address.getStreet());
      preparedStatement.setString(6, address.getHouse());
      preparedStatement.setString(7, address.getApartment());
      return preparedStatement;
    }, keyHolder);
    if (affectedRows == 0) {
      throw new SQLException("Ошибка сохранения адреса, ни одной строки не добавлено");
    }
    var generatedKeys = keyHolder.getKeys();
    if (generatedKeys == null || generatedKeys.size() == 0 || generatedKeys.get("address_id") == null) {
      throw new SQLException("Ошибка сохранения адреса, идентификатор не сгенерирован");
    }
    var generatedId = (Long) generatedKeys.get("address_id");
    address.setAddressId(generatedId);
    return address;
  }

  /**
   * Находит все адреса по идентификатору пользователя.
   *
   * @param userId Идентификатор пользователя
   * @return Список адресов или пустой список, если адресов нет
   */
  @Override
  public List<Address> findAddressByUserId(Long userId) {
    var sql = "SELECT * FROM addresses WHERE user_id = ?";
    RowMapper<Address> rowMapper = (resultSet, rowNum) -> addressMapper.mapResultSetToAddress(resultSet);
    return jdbcTemplate.query(sql, rowMapper, userId);
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
    RowMapper<Address> rowMapper = (resultSet, rowNum) -> addressMapper.mapResultSetToAddress(resultSet);
    var addresses = jdbcTemplate.query(sql, rowMapper, addressId);
    if (addresses.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(addresses.get(0));
    }
  }
}
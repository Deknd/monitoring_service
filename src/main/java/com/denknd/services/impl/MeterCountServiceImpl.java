package com.denknd.services.impl;

import com.denknd.entity.Meter;
import com.denknd.entity.Roles;
import com.denknd.repository.MeterCountRepository;
import com.denknd.security.service.SecurityService;
import com.denknd.services.MeterCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.time.OffsetDateTime;

/**
 * Сервис для работы с информацие о счетчике
 */
@Service
@RequiredArgsConstructor
public class MeterCountServiceImpl implements MeterCountService {
  /**
   * Репозиторий для хранения информации о счетчике
   */
  private final MeterCountRepository meterCountRepository;
  /**
   * Сервис по работе с безопасностью.
   */
  private final SecurityService securityService;

  /**
   * Сохраняет информацию о счетчике
   *
   * @param meter информация о счетчике
   * @return сохраненый объект с информацией о счетчике с идентификатором
   * @throws SQLException ошибка при сохранении информации
   */
  @Override
  public Meter saveMeterCount(Meter meter) throws SQLException {
    meter.setRegistrationDate(OffsetDateTime.now());
    return meterCountRepository.save(meter);
  }

  /**
   * Добавляет дополнительную информацию о счетчике
   *
   * @param meter объект с дополнительной информацией
   * @return возвращает обновленный объект
   * @throws SQLException ошибка при сохранении информации в бд
   */
  @Override
  public Meter addInfoForMeterCount(Meter meter) throws SQLException, AccessDeniedException {
    var userSecurity = securityService.getUserSecurity();
    if (userSecurity.role().equals(Roles.ADMIN)) {
      return this.meterCountRepository.update(meter);
    }
    throw new AccessDeniedException("Доступ разрешен только для пользователей с ролью ADMIN. Ваша роль: "
            + userSecurity.role().name());
  }
}

package com.denknd.services.impl;

import com.denknd.entity.Roles;
import com.denknd.entity.TypeMeter;
import com.denknd.exception.TypeMeterAdditionException;
import com.denknd.repository.TypeMeterRepository;
import com.denknd.security.service.SecurityService;
import com.denknd.services.TypeMeterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.List;

/**
 * Реализация сервиса для работы с типами показаний.
 */
@Service
@RequiredArgsConstructor
public class TypeMeterServiceImpl implements TypeMeterService {
  /**
   * Репозиторий для хранения и получения типов показаний.
   */
  private final TypeMeterRepository typeMeterRepository;

  /**
   * Получает список доступных типов показаний.
   *
   * @return Список доступных типов показаний.
   */
  @Override
  public List<TypeMeter> getTypeMeter() {
    return this.typeMeterRepository.findTypeMeter();
  }

  /**
   * Сервис для работы с безопасностью.
   */
  private final SecurityService securityService;

  /**
   * Добавляет новые типы показаний.
   *
   * @param newType Полностью заполненный объект без идентификатора.
   * @return Полностью заполненный объект с идентификатором.
   * @throws TypeMeterAdditionException при не соблюдения ограничений базы данных
   */
  @Override
  public TypeMeter addNewTypeMeter(TypeMeter newType) throws TypeMeterAdditionException, AccessDeniedException {
    var userSecurity = this.securityService.getUserSecurity();
    if (userSecurity!= null && !userSecurity.role().equals(Roles.ADMIN)) {
      throw new AccessDeniedException(
              "Доступ запрещен, можно добавлять типы показаний пользователю с ролью ADMIN. Ваша роль: "
                      + userSecurity.role().name());
    }
    try {
      return this.typeMeterRepository.save(newType);
    } catch (SQLException e) {
      throw new TypeMeterAdditionException("Ошибка сохранения, введены не верные данные: " + e.getMessage());
    }
  }

  /**
   * Возвращает объект {@link TypeMeter}, полностью заполненный, по переданному коду.
   *
   * @param code Код типа показаний.
   * @return Полностью заполненный объект или null, если не найден.
   */
  @Override
  public TypeMeter getTypeMeterByCode(String code) {
    return this.typeMeterRepository.findTypeMeter()
            .stream()
            .filter(typeMeter -> typeMeter.getTypeCode().equals(code))
            .findFirst()
            .orElse(null);
  }
}

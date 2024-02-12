package com.denknd.aspectj.audit;

import com.denknd.out.audit.AuditService;
import com.denknd.security.service.SecurityService;

/**
 * Класс для конфигурации аспекта аудита
 */
public  class AuditAspectConfig {
  /**
   * Сервис для работы с аудитами
   */
  public static AuditService auditService;
  /**
   * Сервис для работы с безопасностью
   */
  public static SecurityService securityService;

  /**
   * Инициализация сервисов
   * @param auditService Сервис для работы с аудитами
   * @param securityService Сервис для работы с безопасностью
   */
  public static void init(AuditService auditService, SecurityService securityService){
    AuditAspectConfig.auditService = auditService;
    AuditAspectConfig.securityService = securityService;
  }
}

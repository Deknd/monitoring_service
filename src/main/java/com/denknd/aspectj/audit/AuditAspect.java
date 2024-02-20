package com.denknd.aspectj.audit;

import com.denknd.out.audit.Audit;
import com.denknd.out.audit.AuditService;
import com.denknd.security.service.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

/**
 * Класс для логировния действий пользователя через аспект
 */
@Aspect
@Slf4j
@RequiredArgsConstructor
@Component
public class AuditAspect {
  /**
   * Сервис для работы с безоасностью
   */
  private final SecurityService securityService;
  /**
   * Сервис для сохранения аудитов
   */
  private final AuditService auditService;

  /**
   * Метод для формирования аудита и отправки его в сервис
   *
   * @param joinPoint                точка начала работы аудита
   * @param auditRecordingAnnotation информация из аннотации
   * @return возвращает выполнения метода дальше
   * @throws Throwable ошибки возникшие при работе
   */
  @Around("@annotation(auditRecordingAnnotation)")
  public Object audit(ProceedingJoinPoint joinPoint, AuditRecording auditRecordingAnnotation) throws Throwable {
      var auditValue = auditRecordingAnnotation.value();
      var now = OffsetDateTime.now();
      var userSecurity = this.securityService.getUserSecurity();
      var signature = joinPoint.getSignature().getName();
      var operation = "Метод: " + signature + " действие: " + auditValue;
      var audit = Audit.builder().operationTime(now).operation(operation).user(userSecurity).build();
      this.auditService.addAction(audit);
      return joinPoint.proceed();
  }
}

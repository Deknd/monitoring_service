package com.denknd.audit.aspect;

import com.denknd.audit.api.AuditRecording;
import com.denknd.audit.api.UserIdentificationService;
import com.denknd.audit.entity.Audit;
import com.denknd.audit.services.AuditService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.time.OffsetDateTime;

/**
 * Класс для логирования действий пользователя через аспект
 */
@Aspect
@RequiredArgsConstructor
public class AuditAspect {
  private final UserIdentificationService userIdentificationService;
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
    var userId = this.userIdentificationService.getUserId();
    var signature = joinPoint.getSignature().getName();
    var operation = "Метод: " + signature + " действие: " + auditValue;
    var audit = Audit.builder().operationTime(now).operation(operation).userId(userId).build();
    this.auditService.addAction(audit);
    return joinPoint.proceed();
  }
}

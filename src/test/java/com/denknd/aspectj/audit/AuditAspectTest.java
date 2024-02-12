package com.denknd.aspectj.audit;

import com.denknd.out.audit.AuditService;
import com.denknd.security.service.SecurityService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuditAspectTest {
  @Mock
  private SecurityService securityService;
  @Mock
  private AuditService auditService;
  private AuditAspect auditAspect;
  private AutoCloseable autoCloseable;
  @BeforeEach
  void setUp() {
    this.autoCloseable = MockitoAnnotations.openMocks(this);
    AuditAspectConfig.init(this.auditService, this.securityService);
    this.auditAspect = new AuditAspect();
  }

  @AfterEach
  void tearDown() throws Exception {
    this.autoCloseable.close();
  }

  @Test
  @DisplayName("Проверяет, что метод вызывает все сервисы и освобождает выполнения объект")
  void audit() throws Throwable {
    var joinPoint = mock(ProceedingJoinPoint.class);
    var auditRecording = mock(AuditRecording.class);
    when(joinPoint.getSignature()).thenReturn(mock(Signature.class));

    this.auditAspect.audit(joinPoint, auditRecording);

    verify(this.securityService, times(1)).getUserSecurity();
    verify(this.auditService, times(1)).addAction(any());
    verify(joinPoint, times(1)).proceed();
  }
}
package com.denknd.out.audit;

import com.denknd.repository.AuditRepository;
import com.denknd.security.entity.UserSecurity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuditServiceImplTest {
  @Mock
  private AuditRepository auditRepository;
  private AuditServiceImpl auditService;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    this.closeable = MockitoAnnotations.openMocks(this);
    this.auditService = new AuditServiceImpl(this.auditRepository);
  }
  @AfterEach
  void tearDown() throws Exception {
    this.closeable.close();
  }
  @Test
  @DisplayName("Проверяет, что с активным пользователем вызывается репозиторий с собранным объектом аудит")
  void addAction() throws SQLException {
    var audit = mock(Audit.class);

    this.auditService.addAction(audit);

    var auditCaptor = ArgumentCaptor.forClass(Audit.class);
    verify(this.auditRepository, times(1)).save(auditCaptor.capture());
    var auditArgument = auditCaptor.getValue();
    assertThat(auditArgument).isEqualTo(audit);
  }

}
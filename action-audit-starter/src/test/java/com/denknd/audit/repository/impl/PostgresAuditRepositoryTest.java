package com.denknd.audit.repository.impl;

import com.denknd.audit.config.AuditAutoConfiguration;
import com.denknd.audit.config.TestConfig;
import com.denknd.audit.entity.Audit;
import com.denknd.audit.repository.AuditRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {TestConfig.class, AuditAutoConfiguration.class})
@ActiveProfiles("test")
class PostgresAuditRepositoryTest {
  @Autowired
  private AuditRepository auditRepository;

  @Test
  @DisplayName("Проверяет, что сохраняется аудит")
  void save() throws SQLException {
    var audit = Audit.builder()
            .operation("operation test")
            .operationTime(OffsetDateTime.now())
            .userId(1L).build();

    var save = this.auditRepository.save(audit);

    assertThat(save.getAuditId()).isNotNull();
    assertThat(save.getUserId()).isEqualTo(audit.getUserId());

  }

  @Test
  @DisplayName("Проверяется, что с аудитом в котором айди установлено, выходит из метода")
  void save_failed() {
    var audit = Audit.builder().auditId(123L).userId(5L).build();

    assertThatThrownBy(() -> this.auditRepository.save(audit));

  }
}
package com.denknd.out.audit;

import com.denknd.repository.TestContainer;
import com.denknd.security.entity.UserSecurity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostgresAuditRepositoryTest extends TestContainer {

  private PostgresAuditRepository auditRepository;

  @BeforeEach
  void setUp() {
    this.auditRepository = new PostgresAuditRepository(postgresContainer.getDataBaseConnection());
  }


  @Test
  @DisplayName("Проверяет, что сохраняется аудит")
  void save() throws SQLException {
    var user = UserSecurity.builder().userId(2L).build();
    var audit = Audit.builder()
            .operation("operation test")
            .operationTime(OffsetDateTime.now())
            .user(user).build();

    var save = this.auditRepository.save(audit);

    assertThat(save.getAuditId()).isNotNull();
    assertThat(save.getUser()).isEqualTo(user);

  }
  @Test
  @DisplayName("Проверяет, что не сохраняется аудит, если нет пользователя с айди")
  void save_notSaveBecauseNotUser() {

    var user = UserSecurity.builder().userId(12345345L).build();
    var audit = Audit.builder().user(user).build();

    assertThatThrownBy(() ->this.auditRepository.save(audit) ) ;
  }
  @Test
  @DisplayName("Проверяется, что с аудитом в котором айди установлено, выходит из метода")
  void save_failed() {
    var user = UserSecurity.builder().userId(5L).build();
    var audit = Audit.builder().auditId(123L).user(user).build();

    assertThatThrownBy(() ->this.auditRepository.save(audit) ) ;

  }
}
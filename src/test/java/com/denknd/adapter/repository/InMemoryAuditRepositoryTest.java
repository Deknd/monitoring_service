package com.denknd.adapter.repository;

import com.denknd.entity.Audit;
import com.denknd.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryAuditRepositoryTest {
    private InMemoryAuditRepository auditRepository;

    @BeforeEach
    void setUp() {
        this.auditRepository = new InMemoryAuditRepository();
        var user = User.builder().userId(5L).build();
        var audit = Audit.builder().user(user).build();

        this.auditRepository.save(audit);
    }

    @Test
    @DisplayName("Проверяет, что сохраняется аудит")
    void save() {
        var user = User.builder().userId(4L).build();
        var audit = Audit.builder().user(user).build();

        var save = this.auditRepository.save(audit);
        assertThat(save.getAuditId()).isNotNull();
        assertThat(save.getUser()).isEqualTo(user);

    }

    @Test
    @DisplayName("Проверяет, что сохраняется на одного и того же пользователя несколько аудитов")
    void save_addUserRepeat() {

        var user = User.builder().userId(5L).build();
        var audit = Audit.builder().user(user).build();

        var save = this.auditRepository.save(audit);
        assertThat(save.getAuditId()).isNotNull();
        assertThat(save.getUser()).isEqualTo(user);

    }

    @Test
    @DisplayName("Проверяется, что с аудитом в котором айди установлено, выходит из метода")
    void save_failed() {
        var user = User.builder().userId(5L).build();
        var audit = Audit.builder().auditId(123L).user(user).build();

        var save = this.auditRepository.save(audit);
        assertThat(save).isNull();

    }
}
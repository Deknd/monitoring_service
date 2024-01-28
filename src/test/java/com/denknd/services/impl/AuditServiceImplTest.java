package com.denknd.services.impl;

import com.denknd.entity.Audit;
import com.denknd.entity.User;
import com.denknd.in.commands.ConsoleCommand;
import com.denknd.port.AuditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuditServiceImplTest {
    private AuditRepository auditRepository;
    private AuditServiceImpl auditService;

    @BeforeEach
    void setUp() {
        this.auditRepository = mock(AuditRepository.class);
        this.auditService = new AuditServiceImpl(this.auditRepository);
    }

    @Test
    @DisplayName("Проверяет, что с активным пользователем вызывается репозиторий с собранным объектом аудит")
    void addAction() {
        var code = "test";
        var consoleCommand = mock(ConsoleCommand.class);
        when(consoleCommand.getCommand()).thenReturn(code);
        var consoleCommandMap = Map.of(code, consoleCommand);
        var user = mock(User.class);

        this.auditService.addAction(consoleCommandMap, code, user);

        var auditCaptor = ArgumentCaptor.forClass(Audit.class);
        verify(this.auditRepository, times(1)).save(auditCaptor.capture());
        var auditArgument = auditCaptor.getValue();
        assertThat(auditArgument.getOperationTime()).isNotNull();
        assertThat(auditArgument.getUser()).isEqualTo(user);
        assertThat(auditArgument.getOperation()).isNotNull();
        assertThat(auditArgument.getAuditId()).isNull();


    }
    @Test
    @DisplayName("Проверяет, что с активным пользователем вызывается репозиторий с собранным объектом аудит, при выполнении неизвестной команды")
    void addAction_unknownCommand() {
        var code = "tasd";
        var consoleCommand = mock(ConsoleCommand.class);
        when(consoleCommand.getCommand()).thenReturn("code");
        var consoleCommandMap = Map.of("code", consoleCommand);
        var user = mock(User.class);

        this.auditService.addAction(consoleCommandMap, code, user);

        var auditCaptor = ArgumentCaptor.forClass(Audit.class);
        verify(this.auditRepository, times(1)).save(auditCaptor.capture());
        var auditArgument = auditCaptor.getValue();
        assertThat(auditArgument.getOperationTime()).isNotNull();
        assertThat(auditArgument.getUser()).isEqualTo(user);
        assertThat(auditArgument.getOperation()).isNotNull();
        assertThat(auditArgument.getAuditId()).isNull();


    }
    @Test
    @DisplayName("Проверяет, что с не активным пользователем не вызывается репозиторий")
    void addAction_notUser() {
        var code = "tasd";
        var consoleCommand = mock(ConsoleCommand.class);
        when(consoleCommand.getCommand()).thenReturn("code");
        var consoleCommandMap = Map.of("code", consoleCommand);

        this.auditService.addAction(consoleCommandMap, code, null);

        verify(this.auditRepository, times(0)).save(any());



    }
}
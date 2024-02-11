//package com.denknd.out.audit;
//
//import com.denknd.repository.AuditRepository;
//import com.denknd.security.entity.UserSecurity;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.sql.SQLException;
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.*;
//
//class AuditServiceImplTest {
//  @Mock
//  private AuditRepository auditRepository;
//  private AuditServiceImpl auditService;
//  private AutoCloseable closeable;
//
//  @BeforeEach
//  void setUp() {
//    this.closeable = MockitoAnnotations.openMocks(this);
//    this.auditService = new AuditServiceImpl(this.auditRepository);
//  }
//  @AfterEach
//  void tearDown() throws Exception {
//    this.closeable.close();
//  }
//  @Test
//  @DisplayName("Проверяет, что с активным пользователем вызывается репозиторий с собранным объектом аудит")
//  void addAction() throws SQLException {
//    var code = "test";
//    var consoleCommand = mock(ConsoleCommand.class);
//    when(consoleCommand.getCommand()).thenReturn(code);
//    var consoleCommandMap = Map.of(code, consoleCommand);
//    var user = mock(UserSecurity.class);
//
//    this.auditService.addAction(consoleCommandMap, code, user);
//
//    var auditCaptor = ArgumentCaptor.forClass(Audit.class);
//    verify(this.auditRepository, times(1)).save(auditCaptor.capture());
//    var auditArgument = auditCaptor.getValue();
//    assertThat(auditArgument.getOperationTime()).isNotNull();
//    assertThat(auditArgument.getUser()).isEqualTo(user);
//    assertThat(auditArgument.getOperation()).isNotNull();
//    assertThat(auditArgument.getAuditId()).isNull();
//
//
//  }
//
//  @Test
//  @DisplayName("Проверяет, что с активным пользователем вызывается репозиторий с собранным объектом аудит и выкидывает ошибку")
//  void addAction_sqlException() throws SQLException {
//    var code = "test";
//    var consoleCommand = mock(ConsoleCommand.class);
//    when(consoleCommand.getCommand()).thenReturn(code);
//    var consoleCommandMap = Map.of(code, consoleCommand);
//    var user = mock(UserSecurity.class);
//    when(this.auditRepository.save(any())).thenThrow(SQLException.class);
//
//    this.auditService.addAction(consoleCommandMap, code, user);
//
//    var auditCaptor = ArgumentCaptor.forClass(Audit.class);
//    verify(this.auditRepository, times(1)).save(auditCaptor.capture());
//    var auditArgument = auditCaptor.getValue();
//    assertThat(auditArgument.getOperationTime()).isNotNull();
//    assertThat(auditArgument.getUser()).isEqualTo(user);
//    assertThat(auditArgument.getOperation()).isNotNull();
//    assertThat(auditArgument.getAuditId()).isNull();
//
//
//  }
//
//  @Test
//  @DisplayName("Проверяет, что с активным пользователем вызывается репозиторий с собранным объектом аудит, при выполнении неизвестной команды")
//  void addAction_unknownCommand() throws SQLException {
//    var code = "tasd";
//    var consoleCommand = mock(ConsoleCommand.class);
//    when(consoleCommand.getCommand()).thenReturn("code");
//    var consoleCommandMap = Map.of("code", consoleCommand);
//    var user = mock(UserSecurity.class);
//
//    this.auditService.addAction(consoleCommandMap, code, user);
//
//    var auditCaptor = ArgumentCaptor.forClass(Audit.class);
//    verify(this.auditRepository, times(1)).save(auditCaptor.capture());
//    var auditArgument = auditCaptor.getValue();
//    assertThat(auditArgument.getOperationTime()).isNotNull();
//    assertThat(auditArgument.getUser()).isEqualTo(user);
//    assertThat(auditArgument.getOperation()).isNotNull();
//    assertThat(auditArgument.getAuditId()).isNull();
//
//
//  }
//
//  @Test
//  @DisplayName("Проверяет, что с не активным пользователем не вызывается репозиторий")
//  void addAction_notUser() throws SQLException {
//    var code = "tasd";
//    var consoleCommand = mock(ConsoleCommand.class);
//    when(consoleCommand.getCommand()).thenReturn("code");
//    var consoleCommandMap = Map.of("code", consoleCommand);
//
//    this.auditService.addAction(consoleCommandMap, code, null);
//
//    verify(this.auditRepository, times(0)).save(any());
//
//
//  }
//}
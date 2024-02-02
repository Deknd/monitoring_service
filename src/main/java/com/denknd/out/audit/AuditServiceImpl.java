package com.denknd.out.audit;

import com.denknd.in.commands.ConsoleCommand;
import com.denknd.repository.AuditRepository;
import com.denknd.security.UserSecurity;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Сервис для обработки аудита.
 */
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {
  /**
   * Репозиторий аудита.
   */
  private final AuditRepository auditRepository;

  /**
   * Сохраняет аудит в репозиторий.
   *
   * @param consoleCommandMap доступные консольные команды
   * @param commandAndParam   набранная команда
   * @param activeUser        активный пользователь
   */
  @Override
  public void addAction(
          Map<String, ConsoleCommand> consoleCommandMap,
          String commandAndParam,
          UserSecurity activeUser) {
    var iGivingAudit = consoleCommandMap.keySet().stream()
            .filter(commandAndParam::contains)
            .map(consoleCommandMap::get)
            .map(consoleCommand -> (AuditInfoProvider) consoleCommand)
            .findFirst()
            .orElseGet(() -> new AuditInfoProvider() {
              @Override
              public String getCommand() {
                return "неизвестная команда: " + commandAndParam;
              }

              @Override
              public String getAuditActionDescription() {
                return "Неизвестно";
              }

            });
    if (activeUser != null) {
      var operation = String.format("Введенная команда: %s, выполнилась команда: %s - %s",
              commandAndParam, iGivingAudit.getCommand(), iGivingAudit.getAuditActionDescription());

      var audit = Audit.builder()
              .operationTime(OffsetDateTime.now())
              .user(activeUser)
              .operation(operation)
              .build();

      this.auditRepository.save(audit);
    }
  }
}

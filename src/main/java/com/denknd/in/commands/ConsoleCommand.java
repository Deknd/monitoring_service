package com.denknd.in.commands;

import com.denknd.entity.Roles;
import com.denknd.out.audit.AuditInfoProvider;
import com.denknd.security.UserSecurity;

/**
 * Интерфейс представляющий команду консоли, при помощи которого добавляются новые команды для консоли.
 */
public interface ConsoleCommand extends AuditInfoProvider {
  /**
   * Возвращает строку с командой, которая запускает выполнение метода run.
   *
   * @return строка с командой для выполнения действия класса
   */
  String getCommand();

  /**
   * Основной метод класса, выполняющий команду.
   *
   * @param command    команда полученная из консоли
   * @param userActive активный пользователь
   * @return строка, содержащая результат работы метода
   */
  String run(String command, UserSecurity userActive);

  /**
   * Возвращает подсказку для команды help.
   *
   * @param role роль, доступная пользователю
   * @return строка с подсказкой по работе с данной командой
   */
  String getHelpCommand(Roles role);
}

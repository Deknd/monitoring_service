package com.denknd;

import com.denknd.config.ManualConfig;

/**
 * Основной класс для запуска приложения мониторинга.
 */
public class MonitoringServiceApplication {
  /**
   * Метод запуска приложения.
   *
   * @param args аргументы командной строки
   */
  public static void main(String[] args) {
    var context = new ManualConfig();
    var console = context.console();
    console.run();
  }
}
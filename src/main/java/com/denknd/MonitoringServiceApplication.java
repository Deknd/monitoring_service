package com.denknd;

import com.denknd.config.ManualConfig;

import java.io.FileNotFoundException;

/**
 * Основной класс для запуска приложения мониторинга.
 */
public class MonitoringServiceApplication {
  /**
   * Метод запуска приложения.
   *
   * @param args аргументы командной строки
   */
  public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException {
    var context = new ManualConfig(null, null);
    var console = context.console();
    console.run();
  }
}
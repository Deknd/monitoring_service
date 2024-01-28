package com.denknd.port;

/**
 * Интерфейс для работы с аудитом
 */
public interface IGivingAudit {
    /**
     * Команда для выполнения
     * @return возвращает команду для выполнения
     */
   String getCommand();

    /**
     * Для получения краткого описание работы команды
     * @return краткое описание работы команды
     */
    String getMakesAction();
}

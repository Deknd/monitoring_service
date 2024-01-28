package com.denknd.port;

import com.denknd.entity.User;

import java.util.Optional;

/**
 * Интерфейс для работы с пользователями
 */
public interface UserRepository {

    /**
     * Проверяет, существует ли пользователь с таким эмейлом
     * @param email эмейл пользователя
     * @return true если существует
     */
    boolean existUser(String email);

    /**
     * Проверяет, существует ли пользователь с таким айди
     * @param userId айди пользователя
     * @return true если существует
     */
    boolean existUserByUserId(Long userId);

    /**
     * Сохраняет пользователя в бд
     * @param create полностью заполненный объект пользователя, без айди
     * @return объект пользователя с айди
     */
    User save(User create);

    /**
     * Ищет пользователя по емайлу
     * @param email емайл пользователя
     * @return опшинал с возможным пользователем
     */
    Optional<User> find(String email);

    /**
     * Ищет пользователя по айди
     * @param id айди пользователя
     * @return опшинал с возможным пользователем
     */
    Optional<User> findById(Long id);
}

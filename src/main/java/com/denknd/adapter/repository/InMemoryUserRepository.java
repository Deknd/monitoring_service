package com.denknd.adapter.repository;

import com.denknd.entity.User;
import com.denknd.port.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * Сохраняет объект Юзера в память
 */
public class InMemoryUserRepository implements UserRepository {
    /**
     * Хранит связь между айди и объектом
     */
    private final Map<Long, User> userDB = new HashMap<>();
    /**
     * Хранит связь между эмейлом и айди юзера
     */
    private final Map<String, Long> connectionEmailToId = new HashMap<>();
    /**
     * Для генерации айди
     */
    private final Random random = new Random();

    /**
     * Проверяет, существует ли пользователь с данным email
     *
     * @param email электронная почта, для проверки
     * @return true - если существует
     */
    @Override
    public boolean existUser(String email) {
        return this.connectionEmailToId.containsKey(email);
    }

    /**
     * Проверяет, существует ли пользователь с данным айди
     *
     * @param userId айди для проверки
     * @return true - если существует
     */
    @Override
    public boolean existUserByUserId(Long userId) {
        return this.userDB.containsKey(userId);
    }

    /**
     * Сохраняет пользователя в память
     *
     * @param create заполненный объект пользователя, без айди
     * @return возвращает копию сохраненного пользователя
     */
    @Override
    public User save(User create) {
        long userId;
        if (create.getUserId() == null) {

            do {
                userId = Math.abs(this.random.nextLong());

            } while (this.userDB.containsKey(userId));
        } else {
            return null;
        }

        create.setUserId(userId);
        this.userDB.put(userId, create);
        this.connectionEmailToId.put(create.getEmail(), userId);
        return buildUser(create);
    }

    /**
     * Копирует User
     * @param create полностью собранный объект
     * @return копию принятого объекта
     */
    private User buildUser(User create) {
        return User.builder()
                .userId(create.getUserId())
                .firstName(create.getFirstName())
                .lastName(create.getLastName())
                .email(create.getEmail())
                .password(create.getPassword())
                .addresses(create.getAddresses())
                .roles(create.getRoles())
                .build();
    }

    /**
     * Ищет пользователя по емайлу
     * @param email емайл по которому нужно найти
     * @return опшинал с юзером или пустой опшинал
     */
    @Override
    public Optional<User> find(String email) {
        if (!this.connectionEmailToId.containsKey(email)) {
            return Optional.empty();
        }
        var userId = this.connectionEmailToId.get(email);
        return Optional.of(this.userDB.get(userId));
    }

    /**
     * Ищет пользователя по айди
     * @param id айди по которому нужно найти
     * @return опшинал с юзером или пустой опшинал
     */
    @Override
    public Optional<User> findById(Long id) {
        if (this.userDB.containsKey(id)) {
            return Optional.of(this.userDB.get(id));
        }
        return Optional.empty();
    }
}

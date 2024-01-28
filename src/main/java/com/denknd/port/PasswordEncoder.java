package com.denknd.port;

/**
 * Интерфейс для хеширования пароля
 */
public interface PasswordEncoder {
    /**
     * Хеширует пароль
     * @param rawPassword не захешированый пароль
     * @return хеш пароля
     */
    String encode(CharSequence rawPassword);

    /**
     * Сравнивает хешированый пароль с не хешированым
     * @param rawPassword не захешированый пароль
     * @param encodedPassword хеш пароля
     * @return true если они равны
     */
    boolean matches(CharSequence rawPassword, String encodedPassword);
}

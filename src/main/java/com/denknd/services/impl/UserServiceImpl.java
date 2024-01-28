package com.denknd.services.impl;

import com.denknd.entity.User;
import com.denknd.exception.UserAlreadyExistsException;
import com.denknd.port.PasswordEncoder;
import com.denknd.port.UserRepository;
import com.denknd.services.UserService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/**
 * Сервис для работы с юзером
 */
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    /**
     * Репозиторий для взаимодействия с хранилищем пользователей
     */
    private final UserRepository userRepository;
    /**
     * Кодировщик и сравниватель паролей
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Регистрация пользователя
     * @param create полностью заполненный объект пользователя без айди
     * @return полностью заполненный объект пользователя с айди
     * @throws UserAlreadyExistsException если пользователь с таким емайлом существует
     */
    @Override
    public User registrationUser(User create) throws UserAlreadyExistsException {
        if(this.userRepository.existUser(create.getEmail())){
            throw new UserAlreadyExistsException("Данный пользователь уже существует");
        }
        create.setPassword(this.passwordEncoder.encode(create.getPassword()));
        return this.userRepository.save(create);
    }

    /**
     * Аутентифицирует пользователя по введенным данным
     * @param email емайл пользователя
     * @param rawPassword пароль пользователя
     * @return пользователь, если данные подтвердятся
     */
    @Override
    public User loginUser(String email, String rawPassword){
        var optionalUser = this.userRepository.find(email);

        if(optionalUser.isEmpty()){
            return null;
        }
        var user = optionalUser.get();
        if(this.passwordEncoder.matches(rawPassword, user.getPassword())){
            return user;
        } else return null;
    }

    /**
     * Проверяет, существует ли пользователь
     * @param userId идентификатор пользователя
     * @return true сли существует
     */
    @Override
    public boolean existUser(Long userId) {
        return this.userRepository.existUserByUserId(userId);
    }

    /**
     * Получает пользователя из репозитория
     * @param userId идентификатор пользователя
     * @return заполненный пользователь
     */
    @Override
    public User getUserById(Long userId) {

        return this.userRepository.findById(userId).orElse(null);
    }

    /**
     * Получает пользователя по емайлу
     * @param email эмейл по которому получить пользователя
     * @return возвращает объект пользователя
     */
    @Override
    public User getUserByEmail(String email) {
        return this.userRepository.find(email).orElse(null);

    }

    /**
     * Проверяет, существует ли пользователь с данным емайлом
     * @param email емайл пользователя
     * @return true если существует пользователь
     */
    @Override
    public boolean existUserByEmail(String email) {
        return this.userRepository.existUser(email);
    }
}

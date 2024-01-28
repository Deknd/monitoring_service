package com.denknd.services.impl;

import com.denknd.entity.User;
import com.denknd.exception.UserAlreadyExistsException;
import com.denknd.port.PasswordEncoder;
import com.denknd.port.UserRepository;
import com.denknd.services.UserService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registrationUser(User create) throws UserAlreadyExistsException {
        if(this.userRepository.existUser(create.getEmail())){
            throw new UserAlreadyExistsException("Данный пользователь уже существует");
        }
        create.setPassword(this.passwordEncoder.encode(create.getPassword()));
        return this.userRepository.save(create);
    }

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

    @Override
    public boolean existUser(Long userId) {
        return this.userRepository.existUserByUserId(userId);
    }

    @Override
    public User getUserById(Long userId) {

        return this.userRepository.findById(userId).orElse(null);
    }

    @Override
    public User getUserByEmail(String email) {
        return this.userRepository.find(email).orElse(null);

    }

    @Override
    public boolean existUserByEmail(String email) {
        return this.userRepository.existUser(email);
    }
}

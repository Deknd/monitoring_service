package com.denknd.services;

import com.denknd.entity.User;
import com.denknd.exception.UserAlreadyExistsException;

public interface UserService {
    User registrationUser(User create) throws UserAlreadyExistsException;
    User loginUser(String email, String rawPassword);
    boolean existUser(Long userId);

    User getUserById(Long userId);
    User getUserByEmail(String email);
    boolean existUserByEmail(String email);

}

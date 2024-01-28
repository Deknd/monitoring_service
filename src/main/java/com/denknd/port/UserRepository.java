package com.denknd.port;

import com.denknd.entity.User;

import java.util.Optional;


public interface UserRepository {


    boolean existUser(String email);
    boolean existUserByUserId(Long userId);
    User save(User create);
    Optional<User> find(String email);
    Optional<User> findById(Long id);
}

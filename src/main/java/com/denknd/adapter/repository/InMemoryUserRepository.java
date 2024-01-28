package com.denknd.adapter.repository;

import com.denknd.entity.User;
import com.denknd.port.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> userDB;
    private final Map<String, Long> connectionEmailToId;
    private final Random random;

    public InMemoryUserRepository() {
        this.userDB = new HashMap<>();
        this.connectionEmailToId = new HashMap<>();
        this.random = new Random();
    }

    @Override
    public boolean existUser(String email) {
        return this.connectionEmailToId.containsKey(email);
    }

    @Override
    public boolean existUserByUserId(Long userId) {
        return this.userDB.containsKey(userId);
    }

    @Override
    public User save(User create) {
        long userId;
        if(create.getUserId() == null){

            do {
                userId = Math.abs(this.random.nextLong());

            } while (this.userDB.containsKey(userId));
        } else {
            userId = create.getUserId();
        }

        create.setUserId(userId);
        this.userDB.put(userId, create);
        this.connectionEmailToId.put(create.getEmail(), userId);
        return create;
    }

    @Override
    public Optional<User> find(String email) {
        if(!this.connectionEmailToId.containsKey(email)){
            return Optional.empty();
        }
        var userId = this.connectionEmailToId.get(email);
        return Optional.of(this.userDB.get(userId));
    }

    @Override
    public Optional<User> findById(Long id) {
        if(this.userDB.containsKey(id)){
            return Optional.of(this.userDB.get(id));
        }
        return Optional.empty();
    }
}

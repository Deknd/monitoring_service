package com.denknd.port;

import com.denknd.entity.Role;

import java.util.List;
import java.util.Set;

public interface RoleRepository {
    boolean save(Long userId, Role... role);
    List<Role> findRolesByUserId(Long userId);
 }

package com.denknd.services;

import com.denknd.entity.Role;

import java.util.List;
import java.util.Set;

public interface RoleService {

    boolean addRoles(Long userId, Role... roles);
    List<Role> getRoles(Long userId);

    Set<Role> getDefaultRole();

}

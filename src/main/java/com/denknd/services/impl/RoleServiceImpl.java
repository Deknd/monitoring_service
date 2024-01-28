package com.denknd.services.impl;

import com.denknd.entity.Role;
import com.denknd.port.RoleRepository;
import com.denknd.services.RoleService;

import java.util.*;

public class RoleServiceImpl implements RoleService {

    private final Set<Role> defaultRoles;
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository repository) {
        this.roleRepository = repository;
        this.defaultRoles = new HashSet<>();

        var roleUser = Role.builder().roleId(0L).roleName("USER").build();
        var roleADMIN = Role.builder().roleId(1L).roleName("ADMIN").build();

        this.defaultRoles.add(roleUser);
        this.defaultRoles.add(roleADMIN);
    }

    @Override
    public boolean addRoles(Long userId, Role... roles) {
        var roleArray = Arrays.stream(roles)
                .filter(role -> {
                    for (var roleDefault : this.defaultRoles) {
                        if (role.equals(roleDefault)) {
                            return true;
                        }
                    }
                    return false;
                }).toArray(Role[]::new);

        if(roleArray.length == 0){
            return false;
        }

        return this.roleRepository.save(userId, roleArray);
    }

    @Override
    public List<Role> getRoles(Long userId) {
        return this.roleRepository.findRolesByUserId(userId);
    }

    @Override
    public Set<Role> getDefaultRole() {
        return this.defaultRoles;
    }
}

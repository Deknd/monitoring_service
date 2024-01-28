package com.denknd.adapter.repository;

import com.denknd.entity.Role;
import com.denknd.port.RoleRepository;

import java.util.*;

public class InMemoryRoleRepository implements RoleRepository {

    private final Map<Long, Set<Role>> roleUserRelationship = new HashMap<>();
    @Override
    public boolean save(Long userId, Role... roles) {
        if(this.roleUserRelationship.containsKey(userId)){
            var rolesList = this.roleUserRelationship.get(userId);
            rolesList.addAll(Arrays.asList(roles));
        }else {
            this.roleUserRelationship.put(userId, new HashSet<>(Arrays.stream(roles).toList()));
        }
        return true;
    }

    @Override
    public List<Role> findRolesByUserId(Long userId) {
        return this.roleUserRelationship.getOrDefault(userId, Set.of()).stream().toList();
    }
}

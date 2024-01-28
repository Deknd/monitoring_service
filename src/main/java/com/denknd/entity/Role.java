package com.denknd.entity;

import lombok.Builder;
import lombok.Getter;

/**
 * Класс для обозначения роли
 */
@Builder
@Getter
public class Role {
    /**
     * Идентификатор объекта
     */
    private Long roleId;
    /**
     * Имя роли
     */
    private String roleName;


    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Role)) return false;
        final Role other = (Role) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$roleName = this.getRoleName();
        final Object other$roleName = other.getRoleName();
        if (this$roleName == null ? other$roleName != null : !this$roleName.equals(other$roleName)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Role;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $roleId = this.getRoleId();
        result = result * PRIME + ($roleId == null ? 43 : $roleId.hashCode());
        final Object $roleName = this.getRoleName();
        result = result * PRIME + ($roleName == null ? 43 : $roleName.hashCode());
        return result;
    }

    public String toString() {
        return "ROLE_" + this.getRoleName();
    }
}

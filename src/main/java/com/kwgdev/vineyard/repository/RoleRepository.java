package com.kwgdev.vineyard.repository;

import com.kwgdev.vineyard.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * created by kw on 10/17/2020 @ 4:33 PM
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    public Role findRoleByName(String name);
}

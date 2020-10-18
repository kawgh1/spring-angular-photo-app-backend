package com.kwgdev.vineyard.repository;

import com.kwgdev.vineyard.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * created by kw on 10/17/2020 @ 1:43 PM
 */
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    public AppUser findByUsername(String username);

    public AppUser findByEmail(String email);

    @Query("SELECT appUser FROM AppUser appUser WHERE appUser.id=:x")
    public AppUser findUserById(@Param("x") Long id);

    public List<AppUser> findByUsernameContaining(String username);
}

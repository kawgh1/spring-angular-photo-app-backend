package com.kwgdev.vineyard.service.impl;

import com.kwgdev.vineyard.model.AppUser;
import com.kwgdev.vineyard.model.UserRole;
import com.kwgdev.vineyard.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * created by kw on 10/18/2020 @ 11:51 PM
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AccountService accountService;

    // on log in Spring Security will be looking to authenticate the username
    // so here we take the username supplied by user and create a new appUser object
    // and check it against existing users from accountService

    @Override
    public UserDetails loadUserByUsername(String username) {

        AppUser appUser = accountService.findByUsername(username);
        // if the user does not exist, return error
        if (appUser == null) {
            throw new UsernameNotFoundException("Username " + username + " was not found.");
        }
        // Otherwise retrieve authorized user roles for username appUser to give to Spring Security
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        Set<UserRole> userRoles = appUser.getUserRoles();;
        ((Set) userRoles).forEach(userRole -> {

            authorities.add(new SimpleGrantedAuthority(userRoles.toString()));
        });

        // return username as Spring Security User
        return new User(appUser.getUsername(), appUser.getPassword(), authorities);


    }
}

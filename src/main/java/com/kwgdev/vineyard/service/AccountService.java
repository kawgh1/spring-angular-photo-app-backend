package com.kwgdev.vineyard.service;

import com.kwgdev.vineyard.model.AppUser;
import com.kwgdev.vineyard.model.Role;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

/**
 * created by kw on 10/17/2020 @ 1:33 PM
 */
public interface AccountService {

    // auto generated password
     public AppUser saveUser(String name, String username, String email);


    public AppUser findByUsername(String username);

    public AppUser findByEmail(String userEmail);

    public List<AppUser> userList();

    public Role findUserRoleByName(String string);

    public Role saveRole(Role role);

    // user change password
    public void updateUserPassword(AppUser appUser, String newpassword);

    public AppUser updateUser(AppUser user, HashMap<String, String> request);

    public AppUser simpleSaveUser(AppUser user);

    public AppUser findUserById(Long id);

    public void deleteUser(AppUser appUser);

    // auto generated password reset - email
    public void resetPassword(AppUser user);


    public List<AppUser> getUsersListByUsername(String username);

    public String saveUserImage(MultipartFile multipartFile, Long userImageId);


}

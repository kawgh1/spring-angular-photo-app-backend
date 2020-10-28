package com.kwgdev.vineyard.service.impl;

import com.kwgdev.vineyard.model.AppUser;
import com.kwgdev.vineyard.model.Role;
import com.kwgdev.vineyard.model.UserRole;
import com.kwgdev.vineyard.repository.AppUserRepository;
import com.kwgdev.vineyard.repository.RoleRepository;
import com.kwgdev.vineyard.service.AccountService;
import com.kwgdev.vineyard.utility.AmazonClient;
import com.kwgdev.vineyard.utility.Constants;
import com.kwgdev.vineyard.utility.EmailConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * created by kw on 10/17/2020 @ 4:36 PM
 */
@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountService accountService;

    @Autowired
    private AmazonClient amazonClient;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailConstructor emailConstructor;

    @Autowired
    private JavaMailSender mailSender;


// auto generated password
    @Override
    @Transactional
    public AppUser saveUser(String name, String username, String email) {
        String password = RandomStringUtils.randomAlphanumeric(6);
        // save the encrypted password to the database
        String encryptedPassword = bCryptPasswordEncoder.encode(password);

        AppUser appUser = new AppUser();
        appUser.setPassword(encryptedPassword);
        appUser.setName(name);
        appUser.setUsername(username);
        appUser.setEmail(email);
        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(new UserRole(appUser, accountService.findUserRoleByName("USER")));
        appUser.setUserRoles(userRoles);
        appUserRepository.save(appUser);
        // localhost server profile image
//        byte[] bytes;
//        try {
//            bytes = Files.readAllBytes(Constants.TEMP_USER.toPath());
//            String fileName = appUser.getId() + ".png";
//            Path path = Paths.get(Constants.USER_FOLDER + fileName);
//            Files.write(path, bytes);

        String fileName = appUser.getId().toString();
        amazonClient.uploadDefaultProfileImage(fileName);

        // but send the plaintext password to the user email (not ideal, but functional) to confirm
        try {
            mailSender.send(emailConstructor.constructNewUserEmail(appUser, password));
        } catch (Exception e) {
            System.out.println("mail not working");
            e.printStackTrace();
        }


        return appUser;
    }



    @Override
    public void updateUserPassword(AppUser appUser, String newpassword) {
        String encryptedPassword = bCryptPasswordEncoder.encode(newpassword);
        appUser.setPassword(encryptedPassword);
        appUserRepository.save(appUser);
        mailSender.send(emailConstructor.constructResetPasswordEmail(appUser, newpassword));
    }

    @Override
    public AppUser simpleSaveUser(AppUser user) {
        appUserRepository.save(user);
        // send confirm email to user
        mailSender.send(emailConstructor.constructUpdateUserProfileEmail(user));
        return user;

    }

    @Override
    public AppUser findByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    @Override
    public AppUser findByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    @Override
    public List<AppUser> userList() {
        return appUserRepository.findAll();
    }

    @Override
    public Role findUserRoleByName(String name) {
        return roleRepository.findRoleByName(name);
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public AppUser updateUser(AppUser user, HashMap<String, String> request) {
        String name = request.get("name");
        // String username = request.get("username");
        String email = request.get("email");
        String bio = request.get("bio");
        user.setName(name);
        // appUser.setUsername(username);
        user.setEmail(email);
        user.setBio(bio);
        appUserRepository.save(user);
        mailSender.send(emailConstructor.constructUpdateUserProfileEmail(user));
        return user;

    }

    @Override
    public AppUser findUserById(Long id) {
        return appUserRepository.findUserById(id);
    }

    @Override
    public void deleteUser(AppUser appUser) {
        appUserRepository.delete(appUser);

    }

    @Override
    public void resetPassword(AppUser appUser) {

        String password = RandomStringUtils.randomAlphanumeric(6);
        String encryptedPassword = bCryptPasswordEncoder.encode(password);

        appUser.setPassword(encryptedPassword);
        appUserRepository.save(appUser);

        // send confirm email to user
        mailSender.send(emailConstructor.constructResetPasswordEmail(appUser, password));


    }

    @Override
    public List<AppUser> getUsersListByUsername(String username) {
        return appUserRepository.findByUsernameContaining(username);
    }

    @Override
    public String saveUserImage(MultipartFile multipartFile, Long userImageId) {
        /*
         * MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)
         * request; Iterator<String> it = multipartRequest.getFileNames(); MultipartFile
         * multipartFile = multipartRequest.getFile(it.next());
         */

//        localhost server change profile picture
//        byte[] bytes;
//        try {
//            Files.deleteIfExists(Paths.get(Constants.USER_FOLDER + "/" + userImageId + ".png"));
//            bytes = multipartFile.getBytes();
//            Path path = Paths.get(Constants.USER_FOLDER + userImageId + ".png");
//            Files.write(path, bytes);

        try {
            String userImageIdString = userImageId.toString();
            // delete old profile picture
            amazonClient.deleteProfilePicFromS3Bucket(userImageIdString);
            // upload new profile picture
            amazonClient.uploadProfilePic(multipartFile, userImageIdString);
            return "User picture saved to Amazon S3 server!";
        } catch (Exception e) {

            e.printStackTrace();
            return "User profile picture not saved to server. It may be too large!";
        }
    }
}

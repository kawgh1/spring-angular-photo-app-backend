package com.kwgdev.vineyard.resource;

import com.kwgdev.vineyard.model.AppUser;
import com.kwgdev.vineyard.service.AccountService;
import com.sun.mail.iap.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

/**
 * created by kw on 10/17/2020 @ 7:06 PM
 */
@RestController
@RequestMapping("/user")
public class AccountResource {

    private Long userImageId;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    AccountService accountService;



    @GetMapping("/list")
    public ResponseEntity<?> getUsersList() {

        List<AppUser> users = accountService.userList();
        if(users.isEmpty()) {
            return new ResponseEntity<>("No Users Found!", HttpStatus.OK);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserInfo(@PathVariable("username") String username) {

        AppUser user = accountService.findByUsername(username);

        if (user == null) {
            return new ResponseEntity<>("No User Found!", HttpStatus.OK);
        }

        System.out.println("user: " + user);
        System.out.println("username: " + user.getUsername());

        return new ResponseEntity<>(user, HttpStatus.OK);

    }


    @GetMapping("findByUsername/{username}")
    public ResponseEntity<?> getUsersListByUserName(@PathVariable("username") String username) {

        List<AppUser> users = accountService.getUsersListByUsername(username);
        if (users.isEmpty()) {
            return new ResponseEntity<>("No Users Found!", HttpStatus.OK);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }




    //////////////////

    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody HashMap<String, String> request) {

        // we dont want them to be able to create an account if theres already an existing user with that username
        String username = request.get("username");
        if (accountService.findByUsername(username) != null) {
            return new ResponseEntity<>("usernameExist", HttpStatus.CONFLICT);
        }

        String email = request.get("email");

        // allowing multiple accounts per email address because there are no users

//        if (accountService.findByEmail(email) != null) {
//            return new ResponseEntity<>("emailExist", HttpStatus.CONFLICT);
//        }

        String name = request.get("name");


        try {
//            auto generated password
            AppUser user = accountService.saveUser(name, username, email);
            return new ResponseEntity<>(user, HttpStatus.OK );
        }

        catch (Exception e) {
            return new ResponseEntity<>("An error occurred", HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("update")
    public ResponseEntity<?> updateProfile(@RequestBody HashMap<String, String> request) {

        // using user id because we never want to update a different profile. We know user id is UNIQUE and NON NULL
        String id = request.get("id");
        AppUser user = accountService.findUserById(Long.parseLong(id));
        if (user == null) {
            return new ResponseEntity<>("userNotFound", HttpStatus.NOT_FOUND);
        } try {
            accountService.updateUser(user,request);
            userImageId = user.getId();
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {

            return new ResponseEntity<>("An error occurred", HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/photo/upload")
    public ResponseEntity<String> fileUpload(@RequestParam("image") MultipartFile multipartFile) {
        try {
            accountService.saveUserImage(multipartFile, userImageId);
            return new ResponseEntity<>("User Picture Saved!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("User Picture Not Saved", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody HashMap<String, String> request) {
        String username = request.get("username");
        AppUser appUser = accountService.findByUsername(username);
        if (appUser == null) {
            return new ResponseEntity<>("User not found!", HttpStatus.BAD_REQUEST);
        }
        String currentPassword = request.get("currentpassword");
        String newPassword = request.get("newpassword");
        String confirmpassword = request.get("confirmpassword");
        if (!newPassword.equals(confirmpassword)) {
            return new ResponseEntity<>("PasswordNotMatched", HttpStatus.BAD_REQUEST);
        }
        String userPassword = appUser.getPassword();
        try {
            if (newPassword != null && !newPassword.isEmpty() && !StringUtils.isEmpty(newPassword)) {
                if (bCryptPasswordEncoder.matches(currentPassword, userPassword)) {
                    accountService.updateUserPassword(appUser, newPassword);
                }
            } else {
                return new ResponseEntity<>("IncorrectCurrentPassword", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Password Changed Successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error Occured: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<String> resetPassword(@PathVariable("email") String email) {
        AppUser user = accountService.findByEmail(email);
        if (user == null) {
            return new ResponseEntity<String>("emailNotFound", HttpStatus.BAD_REQUEST);
        }
        accountService.resetPassword(user);
        return new ResponseEntity<String>("EmailSent!", HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestBody HashMap<String, String> mapper) {
        String username = mapper.get("username");
        AppUser user = accountService.findByUsername(username);
        accountService.deleteUser(user);
        return new ResponseEntity<String>("User Deleted Successfully!", HttpStatus.OK);
    }

}

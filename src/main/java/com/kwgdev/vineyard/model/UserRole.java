package com.kwgdev.vineyard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

/**
 * created by kw on 10/17/2020 @ 11:58 AM
 */

// class between user and the role
@Entity
public class UserRole implements Serializable {


    private static final long serialVersionUID = 846353L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long userRoleId;

    @ManyToOne // Many userRoles can belong to one user
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.EAGER) // one user role can have many users
    @JoinColumn(name = "role_id")
    private Role role;





    public UserRole() {
    }

    public UserRole(long userRoleId, AppUser appUser, Role role) {
        this.userRoleId = userRoleId;
        this.appUser = appUser;
        this.role = role;
    }

    public UserRole(AppUser appUser, Role role) {
        this.appUser = appUser;
        this.role = role;
    }

    public long getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(long userRoleId) {
        this.userRoleId = userRoleId;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // commented out because this toString method was interfering with authentication headers

//    @Override
//    public String toString() {
//        return "UserRole{" +
//                "userRoleId=" + userRoleId +
//                ", appUser=" + appUser +
//                ", role=" + role +
//                '}';
//    }
}

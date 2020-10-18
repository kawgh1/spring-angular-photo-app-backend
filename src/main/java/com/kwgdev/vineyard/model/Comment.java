package com.kwgdev.vineyard.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * created by kw on 10/17/2020 @ 11:59 AM
 */
@Entity
public class Comment implements Serializable {

    private static final long serialVersionUID = 1646353L;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private Integer id;
    private String username;

    @Column(columnDefinition = "text")
    private String content;

    @CreationTimestamp
    private Date datePosted;

    public Comment() {
    }

    public Comment(Integer id, String username, String content, Date datePosted) {
        this.id = id;
        this.username = username;
        this.content = content;
        this.datePosted = datePosted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", content='" + content + '\'' +
                ", datePosted=" + datePosted +
                '}';
    }
}

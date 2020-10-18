package com.kwgdev.vineyard.repository;

import com.kwgdev.vineyard.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * created by kw on 10/17/2020 @ 1:46 PM
 */
public interface PostRepository extends JpaRepository<Post, Long> {

//    not SQL, this is JPA Query Language HQL?
    @Query("SELECT p FROM Post p order by p.postedDate DESC") // DESC = most recent post first
    public List<Post> findAll();

    @Query("SELECT p FROM Post p WHERE p.username=:username order by p.postedDate DESC")
    public List<Post> findPostByUsername(@Param("username") String username);

    @Query("SELECT p FROM Post p WHERE p.id=:x")
    public Post findPostById(@Param("x") Long id);

    @Modifying // required when doing any HQL that isn't a SELECT statement to the Database
    @Query("DELETE FROM Post WHERE id=:x")
    public void deletePostById(@Param("x") Long id);
}

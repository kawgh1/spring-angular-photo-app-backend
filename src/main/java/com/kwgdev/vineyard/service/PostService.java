package com.kwgdev.vineyard.service;

import com.kwgdev.vineyard.model.AppUser;
import com.kwgdev.vineyard.model.Post;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

/**
 * created by kw on 10/17/2020 @ 1:38 PM
 */
public interface PostService {

    public Post savePost(AppUser appUser, HashMap<String, String> request, String postImageName);

    public List<Post> postList();

    public Post getPostById(Long id);

    public List<Post> findPostByUsername(String username);

    public Post deletePost(Post post);

    public String savePostImage(MultipartFile multipartFile, String fileName);
}

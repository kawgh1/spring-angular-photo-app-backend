package com.kwgdev.vineyard.service.impl;

import com.kwgdev.vineyard.model.AppUser;
import com.kwgdev.vineyard.model.Post;
import com.kwgdev.vineyard.repository.PostRepository;
import com.kwgdev.vineyard.service.PostService;
import com.kwgdev.vineyard.utility.AmazonClient;
import com.kwgdev.vineyard.utility.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * created by kw on 10/17/2020 @ 4:53 PM
 */
@Service
@Transactional
public class PostServiceImpl implements PostService {


    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AmazonClient amazonClient;

    @Override
    public Post savePost(AppUser user, HashMap<String, String> request, String postImageName) {
        String caption = request.get("caption");
        String location = request.get("location");
        Post post = new Post();
        post.setName(postImageName);
        post.setCaption(caption);
        post.setLocation(location);
        post.setUsername(user.getUsername());
        post.setDatePosted(new Date());
        post.setUserImageId(user.getId());
        user.setPost(post);
        postRepository.save(post);
        return post;
    }

    @Override
    public List<Post> postList() {
        return postRepository.findAll();
    }

    @Override
    public Post getPostById(Long id) {
        return postRepository.findPostById(id);
    }

    @Override
    public List<Post> findPostByUsername(String username) {
        return postRepository.findPostByUsername(username);
    }

    // localhost server method
//    @Override
//    public Post deletePost(Post post) {
//        try {
//            Files.deleteIfExists(Paths.get(Constants.POST_FOLDER + "/" + post.getName() + ".png"));
//            postRepository.deletePostById(post.getId());
//            return post;
//        } catch (Exception e) {
//            return null;
//        }
//    }

    // localhost server method
//    @Override
//    public Post deletePost(Post post) {
//        try {
//            Files.deleteIfExists(Paths.get(Constants.POST_FOLDER + "/" + post.getName() + ".png"));
//            postRepository.deletePostById(post.getId());
//            return post;
//        } catch (Exception e) {
//            return null;
//        }
//    }

    @Override
    public Post deletePost(Post post) {
        try {
//            Files.deleteIfExists(Paths.get(Constants.POST_FOLDER + "/" + post.getName() + ".png"));

            // unique image name
            String imageName = post.getName();
            amazonClient.deletePostImageFromS3Bucket(imageName);
            postRepository.deletePostById(post.getId());
            return post;
        } catch (Exception e) {
            return null;
        }
    }

    // localhost server method
//    @Override
//    public String savePostImage(MultipartFile multipartFile, String fileName) {
//
//        /*
//         * MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)
//         * request; Iterator<String> it = multipartRequest.getFileNames(); MultipartFile
//         * multipartFile = multipartRequest.getFile(it.next());
//         */
//
//
//        try {
//            byte[] bytes = multipartFile.getBytes();
//            Path path = Paths.get(Constants.POST_FOLDER + fileName + ".png");
//            Files.write(path, bytes, StandardOpenOption.CREATE);
//        } catch (IOException e) {
//            System.out.println("Error occurred. Photo not saved!");
//            return "Error occurred. Photo not saved!";
//        }
//        System.out.println("Photo saved successfully!");
//        return "Photo saved successfully!";
//    }

    @Override
    public String savePostImage(MultipartFile multipartFile, String postImageName) {

        /*
         * MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)
         * request; Iterator<String> it = multipartRequest.getFileNames(); MultipartFile
         * multipartFile = multipartRequest.getFile(it.next());
         */


        amazonClient.uploadPostImage(multipartFile, postImageName);


        return "Photo saved successfully!";
    }






}
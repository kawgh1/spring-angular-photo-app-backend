package com.kwgdev.vineyard.service;

import com.kwgdev.vineyard.model.Comment;
import com.kwgdev.vineyard.model.Post;

/**
 * created by kw on 10/17/2020 @ 5:31 PM
 */
public interface CommentService {

    public void saveComment(Post post, String username, String content);
}

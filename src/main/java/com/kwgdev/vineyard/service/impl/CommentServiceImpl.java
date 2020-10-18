package com.kwgdev.vineyard.service.impl;

import com.kwgdev.vineyard.model.Comment;
import com.kwgdev.vineyard.model.Post;
import com.kwgdev.vineyard.repository.CommentRepository;
import com.kwgdev.vineyard.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * created by kw on 10/17/2020 @ 5:31 PM
 */

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public void saveComment(Post post, String username, String content) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUsername(username);
        comment.setDatePosted(new Date());
        post.setComments(comment);
        commentRepository.save(comment);
    }
}

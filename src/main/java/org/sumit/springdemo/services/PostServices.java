package org.sumit.springdemo.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sumit.springdemo.model.Post;
import org.sumit.springdemo.repositories.PostRepository;

@Service  // Marks as a Service to use in Spring context
public class PostServices {

    @Autowired
    private PostRepository postRepository;  // Inject repository for DB operations

    public Optional<Post> getById(Long id) {
        return postRepository.findById(id);  // Get post by id
    }

    public List<Post> getAll() {
        return postRepository.findAll();  // Get all posts from DB
    }

    public void delete(Post post) {
        postRepository.delete(post);  // Delete post from DB
    }

    public Post save(Post post) {
        if (post.getId() == null) {
            post.setCreatedAt(LocalDateTime.now());  // Set current time when creating new post
        }
        return postRepository.save(post);  // Save or update post in DB
    }
}

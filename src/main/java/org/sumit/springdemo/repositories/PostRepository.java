package org.sumit.springdemo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sumit.springdemo.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    // Extends JpaRepository to inherit CRUD operations for Post entity
}

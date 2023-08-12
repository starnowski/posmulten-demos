package com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.repositories;

import com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}

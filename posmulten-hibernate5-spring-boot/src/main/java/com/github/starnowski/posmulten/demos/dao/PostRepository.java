package com.github.starnowski.posmulten.demos.dao;

import com.github.starnowski.posmulten.demos.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}

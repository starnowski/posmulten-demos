package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.repositories;

import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}

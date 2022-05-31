package com.github.starnowski.posmulten.demos.dao;

import com.github.starnowski.posmulten.demos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    User findByUsername(String username);
}

package com.github.starnowski.posmulten.demos.services;

import com.github.starnowski.posmulten.demos.dao.UserRepository;
import com.github.starnowski.posmulten.demos.dto.UserDto;
import com.github.starnowski.posmulten.demos.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto create(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user = userRepository.save(user);
        return read(user.getUserId());
    }

    @Transactional(readOnly = true)
    public UserDto read(UUID userId) {
        User user = userRepository.findOne(userId);
        return new UserDto().setUsername(user.getUsername()).setUserId(userId);
    }

}

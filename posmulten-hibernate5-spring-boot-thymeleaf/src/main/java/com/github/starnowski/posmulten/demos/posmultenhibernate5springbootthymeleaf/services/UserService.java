package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.services;

import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.dto.UserDto;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.model.User;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

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
        User user = userRepository.findById(userId).get();
        return new UserDto().setUsername(user.getUsername()).setUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(user -> new UserDto().setUsername(user.getUsername()).setUserId(user.getUserId())).collect(toList());
    }

}

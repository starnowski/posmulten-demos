package com.github.starnowski.posmulten.demos.controllers;

import com.github.starnowski.posmulten.demos.dto.UserDto;
import com.github.starnowski.posmulten.demos.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@ControllerAdvice
@RequestMapping("/app/users")
public class UsersController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createTenant(@RequestBody UserDto body) {
        return new ResponseEntity(userService.create(body), HttpStatus.CREATED);
    }
}

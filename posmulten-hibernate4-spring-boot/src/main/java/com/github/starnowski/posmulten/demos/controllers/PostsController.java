package com.github.starnowski.posmulten.demos.controllers;

import com.github.starnowski.posmulten.demos.dto.PostDto;
import com.github.starnowski.posmulten.demos.dto.UserDto;
import com.github.starnowski.posmulten.demos.security.SecurityServiceImpl;
import com.github.starnowski.posmulten.demos.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ControllerAdvice
@RequestMapping("/app/{domain}/posts")
public class PostsController {

    @Autowired
    private PostService postService;
    @Autowired
    private SecurityServiceImpl securityService;

    @PostMapping
    public ResponseEntity<UserDto> createTenant(@RequestBody PostDto body) {
        return new ResponseEntity(postService.create(body, securityService.findLoggedInTenantUser().getUserId()), HttpStatus.CREATED);
    }

    @GetMapping
    public List<PostDto> list()
    {
        return postService.list();
    }
}

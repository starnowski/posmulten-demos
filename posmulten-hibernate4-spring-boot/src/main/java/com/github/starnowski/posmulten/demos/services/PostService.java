package com.github.starnowski.posmulten.demos.services;

import com.github.starnowski.posmulten.demos.dao.PostRepository;
import com.github.starnowski.posmulten.demos.dao.UserRepository;
import com.github.starnowski.posmulten.demos.dto.PostDto;
import com.github.starnowski.posmulten.demos.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Transactional
    public PostDto create(PostDto postDto, UUID userId)
    {
        Post post = new Post();
        post.setText(postDto.getText());
        post.setAuthor(userRepository.getOne(userId));
        post = postRepository.save(post);
        return get(post.getId());
    }

    private PostDto get(Long postId)
    {
        Post post = postRepository.getOne(postId);
        return new PostDto().setId(post.getId()).setText(post.getText()).setAuthor(userService.read(post.getAuthor().getUserId()));
    }
}

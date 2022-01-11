package com.github.starnowski.posmulten.demos.services;

import com.github.starnowski.posmulten.demos.dao.PostRepository;
import com.github.starnowski.posmulten.demos.dao.UserRepository;
import com.github.starnowski.posmulten.demos.dto.PostDto;
import com.github.starnowski.posmulten.demos.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

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
        return map(post);
    }

    private PostDto map(Post post) {
        return new PostDto().setId(post.getId()).setText(post.getText()).setAuthor(userService.read(post.getAuthor().getUserId()));
    }

    @Transactional(readOnly = true)
    public List<PostDto> list()
    {
        return postRepository.findAll().stream().map(post -> map(post)).collect(toList());
    }
}

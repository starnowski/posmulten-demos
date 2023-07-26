package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.services;

import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.dto.PostDto;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.mappers.PostMapper;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.model.Post;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.repositories.PostRepository;
import jakarta.annotation.PostConstruct;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    private PostMapper postMapper;

    @PostConstruct
    public void postConstructor(){
        this.postMapper = Mappers.getMapper(PostMapper.class);
    }

    @Transactional
    public PostDto create(PostDto dto) {
        Post post = postMapper.mapToEnity(dto);
        post = postRepository.save(post);
        return read(post.getId());
    }

    @Transactional(readOnly = true)
    public PostDto read(Long postId) {
        Post post = postRepository.findById(postId).get();
        return postMapper.mapToDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getAllPosts() {
        return postRepository.findAll().stream().map(user -> postMapper.mapToDto(user)).collect(toList());
    }

}

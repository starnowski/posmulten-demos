package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.mappers;

import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.dto.PostDto;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.model.Post;
import org.mapstruct.Mapper;

@Mapper(uses = UserMapper.class)
public interface PostMapper {

    PostDto mapToDto(Post post);
    Post mapToEnity(PostDto dto);
}

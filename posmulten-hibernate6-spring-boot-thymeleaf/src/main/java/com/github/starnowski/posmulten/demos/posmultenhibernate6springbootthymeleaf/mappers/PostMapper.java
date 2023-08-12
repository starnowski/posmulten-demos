package com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.mappers;

import com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.dto.PostDto;
import com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.model.Post;
import org.mapstruct.Mapper;

@Mapper(uses = UserMapper.class)
public interface PostMapper {

    PostDto mapToDto(Post post);
    Post mapToEnity(PostDto dto);
}

package com.github.starnowski.posmulten.demos.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
@ToString
public class PostDto {

    private long id;
    private UserDto author;
    private String text;
}

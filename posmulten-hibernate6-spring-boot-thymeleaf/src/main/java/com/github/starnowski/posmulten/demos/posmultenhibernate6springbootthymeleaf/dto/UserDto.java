package com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.dto;

import com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.util.RoleEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Set;
import java.util.UUID;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(of = "userId")
@ToString
public class UserDto {

    private UUID userId;
    private String username;
    private String password;
    private Set<RoleEnum> roles;
}

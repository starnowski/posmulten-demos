package com.github.starnowski.posmulten.demos.dto;

import com.github.starnowski.posmulten.demos.util.RoleEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Set;
import java.util.UUID;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(of = "userId")
public class UserDto {

    private UUID userId;
    private String username;
    private String password;
    private Set<RoleEnum> roles;
}

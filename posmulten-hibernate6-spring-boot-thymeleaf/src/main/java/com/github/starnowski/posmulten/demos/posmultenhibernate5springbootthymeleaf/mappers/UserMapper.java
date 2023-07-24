package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.mappers;

import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.dto.UserDto;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.model.User;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.model.UserRole;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.util.RoleEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", qualifiedByName = "roleEnum")
    UserDto mapToDto(User user);
    @Mapping(target = "roles", qualifiedByName = "userRole")
    User mapToEnity(UserDto dto);

    @Named("roleEnum")
    default RoleEnum mapToEnum(UserRole userRole){
        return userRole.getRole();
    }

    @Named("userRole")
    default UserRole mapToUserRole(RoleEnum roleEnum){
        return new UserRole().setRole(roleEnum);
    }
}

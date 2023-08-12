package com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.mappers;

import com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.dto.UserDto;
import com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.model.User;
import com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.model.UserRole;
import com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.util.RoleEnum;
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

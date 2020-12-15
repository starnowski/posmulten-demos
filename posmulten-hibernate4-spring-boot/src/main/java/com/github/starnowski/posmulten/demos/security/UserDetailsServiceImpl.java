package com.github.starnowski.posmulten.demos.security;

import com.github.starnowski.posmulten.demos.dao.UserRepository;
import com.github.starnowski.posmulten.demos.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        Optional.ofNullable(user.getRoles()).orElse(new HashSet<>()).forEach(userRole ->
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + userRole.getRole().name()))
        );

        return new TenantUser(user.getUsername(), user.getPassword(), grantedAuthorities, user.getTenantId());
    }
}

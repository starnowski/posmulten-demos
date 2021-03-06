package com.github.starnowski.posmulten.demos.model;

import com.github.starnowski.posmulten.demos.util.TenantAware;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "user_info")
@NoArgsConstructor
@EqualsAndHashCode(of = "userId")
public class User extends TenantAware {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private UUID userId;
    private String username;
    private String password;
    @OneToMany
    private Set<UserRole> roles;
}

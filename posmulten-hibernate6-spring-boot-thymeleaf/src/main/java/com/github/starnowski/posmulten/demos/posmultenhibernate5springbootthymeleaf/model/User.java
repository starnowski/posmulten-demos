package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "user_info")
@NoArgsConstructor
@EqualsAndHashCode(of = "userId")
public class User {

    @Column(name = "tenant_id", insertable = false, updatable = false)
    private String tenantId;

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private UUID userId;
    private String username;
    private String password;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserRole> roles;
}

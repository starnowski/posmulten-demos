package com.github.starnowski.posmulten.demos.model;

import com.github.starnowski.posmulten.demos.util.RoleEnum;
import com.github.starnowski.posmulten.hibernate.core.TenantTable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "user_role")
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@TenantTable
public class UserRole {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    RoleEnum role;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
